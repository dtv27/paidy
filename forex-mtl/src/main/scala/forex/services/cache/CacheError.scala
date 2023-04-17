package forex.services.cache

sealed trait CacheError extends Throwable

object CacheError {
  case class CacheMissException(message: String) extends CacheError

}
