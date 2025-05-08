import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MealDTO(
    val userId: String,
    val datetime: LocalDateTime,
    val mealType: String,
    val calories: Int,
    val proteins: Int,
    val fats: Int,
    val carbs: Int
)