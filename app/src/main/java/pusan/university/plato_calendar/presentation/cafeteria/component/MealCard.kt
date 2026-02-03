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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.domain.entity.CafeteriaPlan
import pusan.university.plato_calendar.domain.entity.MealType
import pusan.university.plato_calendar.presentation.common.theme.Black
import pusan.university.plato_calendar.presentation.common.theme.Gray
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.WhiteGray
import kotlin.collections.forEach

@Composable
fun MealCard(
    mealType: MealType,
    plans: List<CafeteriaPlan>,
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
            // 공지사항 (운영 안함 or 기타 알림) - 첫 번째 plan만 확인
            val firstPlan = plans.first()

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = mealType.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                )

                if (firstPlan.operatingTime != null) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        painter = painterResource(R.drawable.ic_clock),
                        contentDescription = null,
                        tint = Gray,
                        modifier = Modifier.size(16.dp),
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "운영시간: ${firstPlan.operatingTime}",
                        fontSize = 14.sp,
                        color = Gray,
                    )
                }
            }

            if (!firstPlan.isOperating) {
                Text(
                    text = firstPlan.notOperatingReason ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray,
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    plans.forEach { plan ->
                        Column {
                            // 가격 및 코스 제목
                            plan.courseTitle?.let { title ->
                                Text(
                                    text = title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryColor,
                                )
                            }

                            // 메뉴
                            plan.menus?.let { menus ->
                                Text(
                                    text = menus,
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
