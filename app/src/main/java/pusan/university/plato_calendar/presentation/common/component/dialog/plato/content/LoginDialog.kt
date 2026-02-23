package pusan.university.plato_calendar.presentation.common.component.dialog.plato.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.presentation.common.theme.LightGray
import pusan.university.plato_calendar.presentation.common.theme.MediumGray
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.White

@Composable
fun LoginDialog(
    isLoggingIn: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (LoginCredentials) -> Unit,
) {
    var userName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val isButtonEnabled by remember {
        derivedStateOf {
            userName.isNotBlank() && password.isNotBlank() && !isLoggingIn
        }
    }

    Dialog(onDismissRequest = { if (!isLoggingIn) onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(20.dp),
            ) {
                Text(text = "로그인", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)

                OutlinedTextFieldBackground {
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("아이디") },
                        singleLine = true,
                        enabled = !isLoggingIn,
                        colors =
                            TextFieldDefaults.colors(
                                focusedIndicatorColor = PrimaryColor,
                                unfocusedIndicatorColor = LightGray,
                                focusedContainerColor = LightGray,
                                unfocusedContainerColor = LightGray,
                                cursorColor = PrimaryColor,
                                focusedLabelColor = PrimaryColor,
                                unfocusedLabelColor = PrimaryColor,
                            ),
                        keyboardOptions =
                            KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Next,
                            ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                OutlinedTextFieldBackground {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("비밀번호") },
                        singleLine = true,
                        enabled = !isLoggingIn,
                        visualTransformation = PasswordVisualTransformation(),
                        colors =
                            TextFieldDefaults.colors(
                                focusedIndicatorColor = PrimaryColor,
                                unfocusedIndicatorColor = LightGray,
                                focusedContainerColor = LightGray,
                                unfocusedContainerColor = LightGray,
                                cursorColor = PrimaryColor,
                                focusedLabelColor = PrimaryColor,
                                unfocusedLabelColor = PrimaryColor,
                            ),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(
                        onClick = {
                            onConfirm(LoginCredentials(userName, password))
                        },
                        enabled = isButtonEnabled,
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier =
                            Modifier
                                .width(96.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isLoggingIn) MediumGray else PrimaryColor),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isLoggingIn) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp),
                                )
                            } else {
                                Text(
                                    text = "로그인",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = if (isButtonEnabled) 1f else 0.5f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OutlinedTextFieldBackground(content: @Composable () -> Unit) {
    Box {
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .padding(top = 8.dp)
                    .background(
                        LightGray,
                        shape = RoundedCornerShape(16.dp),
                    ),
        )

        content()
    }
}
