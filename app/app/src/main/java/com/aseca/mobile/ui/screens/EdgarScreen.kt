package com.aseca.mobile.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aseca.mobile.models.EdgarCompany
import com.aseca.mobile.models.EdgarFiling
import com.aseca.mobile.models.EdgarHistoricalMetrics
import com.aseca.mobile.models.EdgarMetricPoint
import com.aseca.mobile.models.EdgarMetrics
import com.aseca.mobile.ui.AuthColors
import com.aseca.mobile.viewmodel.EdgarViewModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

private data class MetricDisplay(
    val title: String,
    val point: EdgarMetricPoint?,
    val money: Boolean,
)

private data class HistoricalMetricDisplay(
    val key: String,
    val title: String,
    val money: Boolean,
    val points: List<EdgarMetricPoint>,
)

@Composable
fun EdgarScreen(
    viewModel: EdgarViewModel,
    accessToken: String,
    onBack: () -> Unit,
) {
    val state = viewModel.uiState

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AuthColors.Background,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                top = 40.dp,
                end = 24.dp,
                bottom = 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                TextButton(onClick = onBack) {
                    Text("← Home", color = AuthColors.MutedText)
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SEC EDGAR",
                        color = AuthColors.Accent,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Buscar empresa",
                        color = AuthColors.PrimaryText,
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Normal,
                    )
                    Text(
                        text = "Consultá métricas, evolución histórica y filings reportados ante la SEC.",
                        color = AuthColors.MutedText,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            item {
                SearchCard(
                    query = state.query,
                    loading = state.loadingSearch,
                    onQueryChange = viewModel::onQueryChange,
                    onSearch = { viewModel.search(accessToken) },
                )
            }

            if (state.error.isNotBlank()) {
                item { EdgarMessage(state.error, error = true) }
            }

            if (state.loadingSearch) {
                item { EdgarLoading("Buscando empresas...") }
            }

            state.selectedCompany?.let { company ->
                item { SelectedCompanyHeader(company) }
            }

            if (state.loadingDetails) {
                item { EdgarLoading("Cargando métricas, histórico y filings...") }
            }

            state.metrics?.let { metrics ->
                item { MetricsSection(metrics) }
            }

            state.historical?.let { historical ->
                item {
                    HistoricalSection(
                        historical = historical,
                        activeMetric = state.activeHistoricalMetric,
                        onMetricSelected = viewModel::selectHistoricalMetric,
                    )
                }
            }

            if (state.filings.isNotEmpty()) {
                item { SectionTitle("Filings recientes") }
                items(
                    items = state.filings.take(6),
                    key = { filing -> filing.accessionNumber },
                ) { filing ->
                    FilingCard(filing)
                }
            }

            if (!state.loadingSearch && state.companies.isEmpty() && state.error.isBlank()) {
                item {
                    EdgarMessage(
                        if (state.hasSearched) {
                            "No hay resultados para mostrar."
                        } else {
                            "Buscá por ticker o nombre para ver resultados."
                        },
                    )
                }
            }

            if (state.companies.isNotEmpty()) {
                item { SectionTitle("Resultados") }
                items(
                    items = state.companies,
                    key = { company -> company.cik },
                ) { company ->
                    CompanyCard(
                        company = company,
                        selected = state.selectedCompany?.cik == company.cik,
                        onClick = { viewModel.selectCompany(accessToken, company) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchCard(
    query: String,
    loading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ej: AAPL, Apple, Microsoft") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuthColors.Accent,
                    unfocusedBorderColor = AuthColors.Border,
                    focusedLabelColor = AuthColors.Accent,
                    unfocusedLabelColor = AuthColors.MutedText,
                    focusedTextColor = AuthColors.PrimaryText,
                    unfocusedTextColor = AuthColors.PrimaryText,
                    cursorColor = AuthColors.Accent,
                ),
            )

            Button(
                onClick = onSearch,
                enabled = !loading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuthColors.Accent,
                    contentColor = Color(0xFF06100B),
                    disabledContainerColor = Color(0xFF183425),
                    disabledContentColor = AuthColors.MutedText,
                ),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(if (loading) "Buscando..." else "Buscar")
            }
        }
    }
}

@Composable
private fun CompanyCard(
    company: EdgarCompany,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (selected) Color(0xFF10291C) else Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, if (selected) AuthColors.Accent else AuthColors.Border),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = company.companyName,
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${company.ticker} · CIK ${company.cik}",
                color = if (selected) AuthColors.Accent else AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SelectedCompanyHeader(company: EdgarCompany) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = company.companyName,
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Ticker ${company.ticker} · CIK ${company.cik}",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Datos reales SEC",
                color = AuthColors.Accent,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun MetricsSection(metrics: EdgarMetrics) {
    val items = listOf(
        MetricDisplay("Revenue", metrics.revenue, money = true),
        MetricDisplay("Net Income", metrics.netIncome, money = true),
        MetricDisplay("EPS", metrics.eps, money = false),
        MetricDisplay("Total Assets", metrics.totalAssets, money = true),
        MetricDisplay("Total Liabilities", metrics.totalLiabilities, money = true),
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Métricas financieras")
        items.forEach { metric ->
            MetricCard(metric)
        }
    }
}

@Composable
private fun MetricCard(metric: MetricDisplay) {
    val value = metric.point?.value
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = metric.title,
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = if (metric.money) value.moneyOrUnavailable() else value.numberOrUnavailable(),
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            metric.point?.let { point ->
                Text(
                    text = listOfNotNull(point.fp, point.fy?.toString(), point.form).joinToString(" · "),
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun HistoricalSection(
    historical: EdgarHistoricalMetrics,
    activeMetric: String,
    onMetricSelected: (String) -> Unit,
) {
    val groups = listOf(
        HistoricalMetricDisplay("revenue", "Revenue", money = true, points = historical.revenue),
        HistoricalMetricDisplay("netIncome", "Net Income", money = true, points = historical.netIncome),
        HistoricalMetricDisplay("eps", "EPS", money = false, points = historical.eps),
        HistoricalMetricDisplay("totalAssets", "Total Assets", money = true, points = historical.totalAssets),
        HistoricalMetricDisplay(
            "totalLiabilities",
            "Total Liabilities",
            money = true,
            points = historical.totalLiabilities,
        ),
    )
    val selectedMetric = groups.firstOrNull { metric -> metric.key == activeMetric } ?: groups.first()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Evolución histórica")

        MetricTabs(
            metrics = groups,
            activeMetric = selectedMetric.key,
            onMetricSelected = onMetricSelected,
        )

        HistoricalDashboardCard(
            metric = selectedMetric,
        )
    }
}

@Composable
private fun MetricTabs(
    metrics: List<HistoricalMetricDisplay>,
    activeMetric: String,
    onMetricSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        metrics.chunked(2).forEach { rowMetrics ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowMetrics.forEach { metric ->
                    MetricTabButton(
                        metric = metric,
                        selected = metric.key == activeMetric,
                        onClick = { onMetricSelected(metric.key) },
                        modifier = Modifier.weight(1f),
                    )
                }

                if (rowMetrics.size == 1) {
                    Column(modifier = Modifier.weight(1f)) {}
                }
            }
        }
    }
}

@Composable
private fun MetricTabButton(
    metric: HistoricalMetricDisplay,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF10291C) else Color(0xFF0C1017),
            contentColor = if (selected) AuthColors.Accent else AuthColors.PrimaryText,
        ),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, if (selected) AuthColors.Accent else AuthColors.Border),
    ) {
        Text(
            text = metric.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun HistoricalDashboardCard(
    metric: HistoricalMetricDisplay,
) {
    val points = metric.points.take(8)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = metric.title,
                        color = AuthColors.PrimaryText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Últimos puntos reportados",
                        color = AuthColors.MutedText,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                points.firstOrNull()?.value?.let { latestValue ->
                    Text(
                        text = latestValue.formatMetric(metric.money),
                        color = AuthColors.Accent,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (points.isEmpty()) {
                Text(
                    text = "No disponible",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                TrendChart(
                    points = points,
                    money = metric.money,
                )

                points.take(5).forEach { point ->
                    MetricPointRow(point, metric.money)
                }
            }
        }
    }
}

@Composable
private fun TrendChart(
    points: List<EdgarMetricPoint>,
    money: Boolean,
) {
    val chartPoints = points
        .filter { point -> point.value != null }
        .take(8)
        .reversed()

    if (chartPoints.size < 2) return

    val values = chartPoints.mapNotNull { point -> point.value }
    val minValue = values.minOrNull() ?: return
    val maxValue = values.maxOrNull() ?: return
    val range = (maxValue - minValue).takeIf { value -> value != 0.0 } ?: 1.0

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
        ) {
            val widthStep = size.width / (values.size - 1)
            val topPadding = 18f
            val bottomPadding = 24f
            val chartHeight = size.height - topPadding - bottomPadding

            val offsets = values.mapIndexed { index, value ->
                val normalized = ((value - minValue) / range).toFloat()
                Offset(
                    x = index * widthStep,
                    y = topPadding + chartHeight - (normalized * chartHeight),
                )
            }

            val areaPath = Path().apply {
                moveTo(offsets.first().x, size.height - bottomPadding)
                offsets.forEach { offset -> lineTo(offset.x, offset.y) }
                lineTo(offsets.last().x, size.height - bottomPadding)
                close()
            }

            val linePath = Path().apply {
                moveTo(offsets.first().x, offsets.first().y)
                offsets.drop(1).forEach { offset -> lineTo(offset.x, offset.y) }
            }

            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AuthColors.Accent.copy(alpha = 0.34f),
                        AuthColors.Accent.copy(alpha = 0.04f),
                    ),
                ),
            )

            drawPath(
                path = linePath,
                color = Color(0xFF8EA2FF),
                style = Stroke(width = 5f, cap = StrokeCap.Round),
            )

            offsets.forEach { offset ->
                drawCircle(
                    color = AuthColors.PrimaryText,
                    radius = 4.5f,
                    center = offset,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = chartPoints.first().end ?: "Inicio",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "${values.last().formatMetric(money)} último",
                color = AuthColors.Accent,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = chartPoints.last().end ?: "Último",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun MetricPointRow(
    point: EdgarMetricPoint,
    money: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = point.end ?: "Sin fecha",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = point.form ?: "Sin form",
                color = AuthColors.Accent,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = if (money) point.value.moneyOrUnavailable() else point.value.numberOrUnavailable(),
            color = AuthColors.PrimaryText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun FilingCard(filing: EdgarFiling) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = filing.form,
                color = AuthColors.Accent,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Filing date: ${filing.filingDate}",
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.bodyMedium,
            )
            filing.reportDate?.let { reportDate ->
                Text(
                    text = "Report date: $reportDate",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                text = filing.link,
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = AuthColors.PrimaryText,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun EdgarLoading(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(color = AuthColors.Accent, strokeWidth = 2.dp)
        Text(
            text = text,
            color = AuthColors.MutedText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun EdgarMessage(
    text: String,
    error: Boolean = false,
) {
    Text(
        text = text,
        color = if (error) AuthColors.Error else AuthColors.MutedText,
        style = MaterialTheme.typography.bodyMedium,
    )
}

private fun Double?.moneyOrUnavailable(): String {
    return this?.let { moneyFormatter.format(it) } ?: "No disponible"
}

private fun Double?.numberOrUnavailable(): String {
    return this?.let { numberFormatter.format(it) } ?: "No disponible"
}

private fun Double.formatMetric(money: Boolean): String {
    return if (money) moneyFormatter.format(this) else numberFormatter.format(this)
}

private val moneyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
    currency = Currency.getInstance("USD")
    maximumFractionDigits = 2
}

private val numberFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
    maximumFractionDigits = 2
}
