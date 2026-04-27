package pusan.university.plato_calendar.presentation.cafeteria.component

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.domain.entity.DormitoryMealInfo
import pusan.university.plato_calendar.presentation.util.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.util.theme.Gray
import pusan.university.plato_calendar.presentation.util.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.util.theme.WhiteGray

private fun buildDormitoryMealShareText(dormitoryName: String, mealInfo: DormitoryMealInfo): String =
    buildString {
        appendLine(dormitoryName)
        append("[${mealInfo.mealType.title}]")
        mealInfo.operatingTime?.let { append(" $it") }
        appendLine()
        append(mealInfo.menus)
    }

private fun shareDormitoryMealText(context: Context, text: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}

@Composable
fun DormitoryMealCard(
    mealInfo: DormitoryMealInfo,
    dormitoryName: String,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = mealInfo.mealType.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                )

                mealInfo.operatingTime?.let { operatingTime ->
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_clock),
                        contentDescription = null,
                        tint = Gray,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = operatingTime,
                        fontSize = 14.sp,
                        color = Gray,
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
                            shareDormitoryMealText(context, buildDormitoryMealShareText(dormitoryName, mealInfo))
                        },
                )
            }

            Text(
                text = mealInfo.menus,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
