package com.crosspaste.ui.paste.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.crosspaste.app.DesktopAppWindowManager
import org.koin.compose.koinInject

@Composable
fun PasteDetailView(
    detailView: @Composable () -> Unit,
    detailInfoView: @Composable () -> Unit,
) {
    val appWindowManager = koinInject<DesktopAppWindowManager>()

    val dpSize by remember { mutableStateOf(appWindowManager.searchWindowDetailViewDpSize) }
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier.size(dpSize).padding(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
        ) {
            detailView()
        }

        Divider(
            modifier = Modifier.fillMaxWidth().height(1.dp),
            thickness = 2.dp,
        )

        Column(
            modifier =
                Modifier.fillMaxWidth().padding(10.dp),
        ) {
            detailInfoView()
        }
    }
}
