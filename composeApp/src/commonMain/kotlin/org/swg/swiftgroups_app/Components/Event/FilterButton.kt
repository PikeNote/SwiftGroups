import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.swg.swiftgroups_app.Fonts.AppFont

@Composable
fun FilterButton(
    label: String,
    icon: ImageVector? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .height(36.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = if (selected) Color(0xFFEEEEEE) else Color.Transparent,
            contentColor = if (selected) Color.Black else Color(0xFF666666)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Color.Black else Color(0xFFCCCCCC)
        )
    ) {
        icon?.let {
            Icon(it, contentDescription = label, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(label, style = AppFont.InterTypography.body2)
    }
}

data class EventFilterTemp(
    val label: String,
    val selected: Boolean,
    val displayLabel: String = label,
    val icon: ImageVector? = null,
    val onClick: () -> Unit
)