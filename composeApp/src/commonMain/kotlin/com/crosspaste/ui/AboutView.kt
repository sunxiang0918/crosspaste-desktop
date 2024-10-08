package com.crosspaste.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crosspaste.app.AppInfo
import com.crosspaste.app.AppInfoFactory
import com.crosspaste.app.AppUrls
import com.crosspaste.i18n.GlobalCopywriter
import com.crosspaste.ui.base.Fonts.ROBOTO_FONT_FAMILY
import com.crosspaste.ui.base.UISupport
import com.crosspaste.ui.base.chevronRight
import org.koin.compose.koinInject

@Composable
fun AboutView(currentPageViewContext: MutableState<PageViewContext>) {
    WindowDecoration(currentPageViewContext, "about")
    AboutContentView()
}

@Composable
fun AboutContentView() {
    val appInfoFactory = koinInject<AppInfoFactory>()
    val appInfo = koinInject<AppInfo>()
    val appUrls = koinInject<AppUrls>()
    val uiSupport = koinInject<UISupport>()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier.align(Alignment.Center)
                    .offset(y = (-30).dp),
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    modifier =
                        Modifier.clip(RoundedCornerShape(6.dp))
                            .size(72.dp),
                    painter = painterResource("crosspaste_icon.png"),
                    contentDescription = "crosspaste icon",
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "CrossPaste",
                    style =
                        TextStyle(
                            fontFamily = ROBOTO_FONT_FAMILY,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                    color = MaterialTheme.colors.onBackground,
                )
                Spacer(modifier = Modifier.height(12.dp))

                val revision = if (appInfoFactory.getRevision() == "Unknown") "" else " (${appInfoFactory.getRevision()})"
                Text(
                    text = "version: ${appInfo.appVersion}$revision",
                    style =
                        TextStyle(
                            fontFamily = ROBOTO_FONT_FAMILY,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                        ),
                    color = MaterialTheme.colors.onBackground,
                )
                Spacer(modifier = Modifier.height(30.dp))

                AboutInfoItem("official_website") {
                    uiSupport.openCrossPasteWebInBrowser()
                }

                Divider(modifier = Modifier.padding(horizontal = 80.dp))

                AboutInfoItem("newbie_tutorial") {
                    uiSupport.openCrossPasteWebInBrowser("tutorial/pasteboard")
                }

                Divider(modifier = Modifier.padding(horizontal = 80.dp))

                AboutInfoItem("change_log") {
                    uiSupport.openUrlInBrowser(appUrls.changeLogUrl)
                }

                Divider(modifier = Modifier.padding(horizontal = 80.dp))

                AboutInfoItem("feedback") {
                    uiSupport.openUrlInBrowser(appUrls.issueTrackerUrl)
                }

                Divider(modifier = Modifier.padding(horizontal = 80.dp))

                AboutInfoItem("contact_us") {
                    uiSupport.openEmailClient("compile.future@gmail.com")
                }
            }
        }
    }
}

@Composable
fun AboutInfoItem(
    title: String,
    onClick: () -> Unit,
) {
    val copywriter = koinInject<GlobalCopywriter>()
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 80.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    onClick()
                },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.wrapContentSize().padding(start = 5.dp),
            text = copywriter.getText(title),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground,
            fontSize = 15.sp,
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = chevronRight(),
            contentDescription = "chevron right",
            tint = MaterialTheme.colors.onBackground,
        )
    }
}
