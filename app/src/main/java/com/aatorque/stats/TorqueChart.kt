package com.aatorque.stats

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatorque.stats.databinding.FragmentChartBinding
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.DataPointInterface
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.parcelize.Parcelize
import java.util.Date
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class TorqueChart: Fragment() {

    @Parcelize
    data class LegendBinding(
        var color: Int,
        var label: String,
        var value: String,
        var icon: Int,
    ) : Parcelable

    private val series = HashMap<TorqueData, LineGraphSeries<DataPointInterface>>()
    private lateinit var graph: GraphView
    private lateinit var binding: FragmentChartBinding
    private var startDate by Delegates.notNull<Long>()
    lateinit var legendBinding: List<LegendBinding>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startDate = Date().time
        graph = view.findViewById(R.id.graph)
        graph.legendRenderer.isVisible = true;
        graph.legendRenderer.align = LegendRenderer.LegendAlign.TOP
        graph.legendRenderer.textColor = Color.WHITE
        graph.legendRenderer.textSize = 25f
        graph.legendRenderer.width = graph.width
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true
        graph.gridLabelRenderer.labelFormatter = object: DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                if (isValueX) {
                    val diff = (value * 0.001) - (startDate * 0.001)
                    val minutes = diff.div(if (diff < 0) -60 else 60).toInt()
                    val seconds = (diff % 60).absoluteValue.roundToInt()
                    return "%02d:%02d".format(minutes, seconds)
                }
                return super.formatLabel(value, false)
            }
        }
        graph.gridLabelRenderer.numHorizontalLabels = 8
        graph.gridLabelRenderer.numVerticalLabels = 7
        graph.gridLabelRenderer.gridColor = Color.TRANSPARENT
        graph.gridLabelRenderer.horizontalLabelsColor = Color.WHITE
        graph.gridLabelRenderer.verticalLabelsColor = Color.WHITE
        graph.gridLabelRenderer.isHorizontalLabelsVisible = true
    }

    fun setupItems(torqueData: Array<TorqueData>) {
        graph.series.clear()
        series.clear()
        val colors = arrayOf(R.color.primaryColor, R.color.primaryDarkColor, R.color.primaryLightColor).map {
            resources.getColor(it, context?.theme)
        }
        binding.labelWrap.removeAllViews()
        legendBinding = torqueData.mapIndexed { idx, data ->
            val line = LineGraphSeries<DataPointInterface>()
            line.title = data.display.label
            line.isDrawDataPoints = false
            line.color = colors[idx]
            graph.addSeries(line)
            series[data] = line
            val binding = LegendBinding(
                color = line.color,
                label = data.display.label,
                icon = try {
                    resources.getIdentifier(
                        data.getDrawableName(),
                        "drawable",
                        requireContext().packageName,
                    )
                } catch (e: Resources.NotFoundException) {
                    R.drawable.ic_none
                },
                value = "-"
            )
            data.notifyUpdate = {
                notifyUpdate(it, binding)
            }
            binding
        }
        binding.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.legendData = LegendAdapter(legendBinding)
        graph.viewport.setMinX(startDate - 40_000.0)
    }

    fun notifyUpdate(data: TorqueData, binding: LegendBinding) {
        val line = series[data]
        val now = Date()
        line?.appendData(DataPoint(now, data.lastData), true, 400)
        binding.value = "${data.lastDataStr}${data.display.unit}"
    }
}