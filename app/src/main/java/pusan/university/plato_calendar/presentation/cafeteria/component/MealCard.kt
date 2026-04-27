package pusan.university.plato_calendar.presentation.cafeteria.component

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.domain.entity.CourseMenu
import pusan.university.plato_calendar.domain.entity.MealInfo
import pusan.university.plato_calendar.domain.entity.MealType
import pusan.university.plato_calendar.domain.entity.OperationInfo
import pusan.university.plato_calendar.presentation.util.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.util.theme.Black
import pusan.university.plato_calendar.presentation.util.theme.Gray
import pusan.university.plato_calendar.presentation.util.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.util.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.util.theme.WhiteGray

private fun buildMealShareText(cafeteriaName: String, mealInfo: MealInfo): String =
    buildString {
        appendLine(cafeteriaName)
        append("[${mealInfo.mealType.title}]")
        mealInfo.operationInfo.operatingTime?.let { append(" $it") }
        appendLine()
        if (!mealInfo.operationInfo.isOperating) {
            append(mealInfo.operationInfo.notOperatingReason ?: "")
        } else {
            mealInfo.courseMenus.forEachIndexed { index, course ->
                if (index > 0) appendLine()
                course.courseTitle?.let { appendLine(it) }
                course.menus?.let { append(it) }
            }
        }
    }

private fun shareMealText(context: Context, text: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}

@Composable
fun MealCard(
    mealInfo: MealInfo,
    cafeteriaName: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(20.dp),
        ) {
            with(mealInfo) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = mealType.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                    )

                    operationInfo.operatingTime?.let {
                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null,
                            tint = Gray,
                            modifier = Modifier.size(16.dp),
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = operationInfo.operatingTime,
                            fontSize = 14.sp,
                            color = Gray,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        painter = painterResource(R.drawable.ic_share),
                        contentDescription = null,
                        tint = Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .noRippleClickable {
                                shareMealText(context, buildMealShareText(cafeteriaName, mealInfo))
                            },
                    )
                }

                if (!operationInfo.isOperating) {
                    Text(
                        text = operationInfo.notOperatingReason ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray,
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        courseMenus.forEach { plan ->
                            Column {
                                plan.courseTitle?.let {
                                    Text(
                                        text = plan.courseTitle,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryColor,
                                    )
                                }

                                plan.menus?.let {
                                    Text(
                                        text = plan.menus,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun MealCardOperatingPreview() {
    PlatoCalendarTheme {
        MealCard(
            cafeteriaName = "금정 학생",
            mealInfo = MealInfo(
                mealType = MealType.LUNCH,
                operationInfo = OperationInfo(
                    isOperating = true,
                    notOperatingReason = null,
                    operatingTime = "11:30 ~ 13:30"
                ),
                courseMenus = listOf(
                    CourseMenu(
                        courseTitle = "정식",
                        menus = "김치찌개, 쌀밥, 배추김치, 계란후라이"
                    ),
                    CourseMenu(
                        courseTitle = "일품",
                        menus = "된장찌개, 잡곡밥, 깍두기, 불고기"
                    )
                )
            )
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun MealCardNotOperatingPreview() {
    PlatoCalendarTheme {
        MealCard(
            cafeteriaName = "금정 학생",
            mealInfo = MealInfo(
                mealType = MealType.DINNER,
                operationInfo = OperationInfo(
                    isOperating = false,
                    notOperatingReason = "미운영",
                    operatingTime = null
                ),
                courseMenus = emptyList()
            )
        )
    }
}
