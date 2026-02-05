package pusan.university.plato_calendar.presentation.cafeteria.component

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.domain.entity.CourseMenu
import pusan.university.plato_calendar.domain.entity.MealInfo
import pusan.university.plato_calendar.domain.entity.MealType
import pusan.university.plato_calendar.domain.entity.OperationInfo
import pusan.university.plato_calendar.presentation.common.theme.Black
import pusan.university.plato_calendar.presentation.common.theme.Gray
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.WhiteGray

@Composable
fun MealCard(
    mealInfo: MealInfo,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null,
                            tint = Gray,
                            modifier = Modifier.size(16.dp),
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "운영시간: ${operationInfo.operatingTime}",
                            fontSize = 14.sp,
                            color = Gray,
                        )
                    }
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
