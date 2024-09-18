//> using scala 3.3

package storky

/** An object with methods for creating a thread-safe, atomic, in-memory key value store. **/
object Store:
  /** Create an empty Store with keys of type K and values of type V.*/
  def empty[K, V](): Store[K, V] = new Store[K, V]()

  /** Create a new Store[K, V] from a byteString of a Store serialized with toByteString. */
  def fromByteString[K, V](s: String): Store[K, V] = 
    val bs: Array[Byte] = java.util.Base64.getDecoder().decode(s)
    val bis = java.io.ByteArrayInputStream(bs)
    var in: java.io.ObjectInput = null
    try 
      in = java.io.ObjectInputStream(bis)
      in.readObject().asInstanceOf[Store[K,V]]
    finally if in != null then in.close()

/** A thread-safe, in-memory key value store with keys of type K and values of type V. */
@SerialVersionUID(123L)
final class Store[K, V] private () extends Serializable:
  import scala.jdk.CollectionConverters.*
  import scala.jdk.FunctionConverters.*

  private val chm = new java.util.concurrent.ConcurrentHashMap[K,V]

  /** Add a key value pair (k, v), return old value Some(v) or None if non-existing. */
  def put(k: K, v: V): Option[V] = Option(chm.put(k, v))

  /** Retrieve the value Some(v) of key k or None if if non-existing. */
  def get(k: K): Option[V] = if (chm.containsKey(k)) Some(chm.get(k)) else None

  /** Remove a key and value associated with k, return old value Some(v) or None if non-existing*/
  def remove(k: K): Option[V] = Option(chm.remove(k)) 

  /** Update k -> v atomically to k -> f(Option(v)). 
    *
    * The old value Some(v) is given as argument to f 
    * If value is absent then the argument to f is None.
    * The new value computed by f is returned wrapped in an Option. 
    * If f returns None then the key is removed. 
    */ 
  def update(k: K)(f: Option[V] => Option[V]): Option[V] = 
    val g: (K, V) => V = (k,v) => f(Option(v)).getOrElse(null.asInstanceOf[V])
    Option(chm.compute(k, g.asJava))

  /** Update all k -> v atomically to k -> f(k, v). */
  def updateAll(f: (K, V) => V): Unit = chm.replaceAll(f.asJava)

  /** Copy all key-value pairs to a scala.collection.immutable.Map */
  def toMap: Map[K, V] = chm.asScala.toMap

  /** An iterator over all keys in the store */
  def keys: Iterable[K] = chm.asScala.keys

  /** An iterator over all values in the store */
  def values: Iterable[V] = chm.asScala.values
  
  /** The number of stored key value pairs */
  def size: Int = chm.size

  /** Remove all stored key value pairs */
  def clear(): Unit = chm.clear()

  /** Create a string representation of the store */
  override def toString: String = toMap.toString

  /** Serialize this Store to a byte string encoded using Base64 */
  def toByteString: String = 
    val bs = java.io.ByteArrayOutputStream()
    val os = java.io.ObjectOutputStream(bs)
    os.writeObject(this)
    os.close()
    java.util.Base64.getEncoder().encodeToString(bs.toByteArray)

end Store