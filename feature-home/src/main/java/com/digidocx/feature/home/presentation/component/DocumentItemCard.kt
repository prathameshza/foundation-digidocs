package com.digidocx.feature.home.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digidocx.core.ui.theme.*
import com.digidocx.feature.home.domain.model.HomeItem

@Composable
fun DocumentItemCard(
    file: HomeItem.File,
    isGridView: Boolean,
    onClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isGridView) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            modifier = modifier
                .padding(6.dp)
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onActionClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = MutedText)
                    }
                }
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(52.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = file.name,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(text = file.size, color = MutedText, fontSize = 11.sp)
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardBackground)
                .clickable { onClick() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = AccentPurple,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = file.name, fontWeight = FontWeight.SemiBold, color = PrimaryText, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = file.size, color = MutedText, fontSize = 12.sp)
                    Text(text = file.dateModified, color = MutedText, fontSize = 12.sp)
                }
            }
            IconButton(onClick = onActionClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = MutedText)
            }
        }
    }
}