package social.utils

/**
 * For enum objects to ensure .values method/field
 */
trait EnumLike[T] {
   def values: Seq[T]
}
