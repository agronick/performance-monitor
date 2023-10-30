package com.aatorque.stats

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aatorque.stats.databinding.FragmentChartBinding
import com.google.common.collect.ImmutableList
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.DataPointInterface
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.Date
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class TorqueChart: Fragment() {

    class LegendBinding : ViewModel() {
        var color = MutableLiveData<Int>()
        var label = MutableLiveData<String>()
        var value = MutableLiveData<String>()
        var icon = MutableLiveData<Int>()
    }

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
        graph.legendRenderer.isVisible = false
        graph.legendRenderer.width = graph.width
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true
        graph.gridLabelRenderer.labelFormatter = object: DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                if (isValueX) {
                    val diff = (value * 0.001) - (startDate * 0.001)
                    val minutes = diff.div(60).toInt().absoluteValue
                    val seconds = (diff % 60).absoluteValue.roundToInt()
                    return "%s%02d:%02d".format(if (diff < 0) "-" else "", minutes, seconds)
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
        graph.gridLabelRenderer.isVerticalLabelsVisible = false

        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            var previousCanvasHeight = graph.height
            var previousCanvasWidth = graph.width
            override fun onGlobalLayout() {
                val canvasWidth = graph.width
                val canvasHeight = graph.height

                // Check if the size of the canvas has shrunk.
                if (canvasWidth != previousCanvasWidth || canvasHeight != previousCanvasHeight) {
                    previousCanvasHeight = canvasHeight
                    previousCanvasWidth = canvasWidth
                }
            }
        })
    }

    fun setupItems(torqueData: Array<TorqueData>) {
        graph.series.clear()
        series.clear()
        val colors = arrayOf(R.color.graphColor1, R.color.graphColor2, R.color.graphColor3).map {
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

            val iconRes = try {
                resources.getIdentifier(
                    data.getDrawableName(),
                    "drawable",
                    requireContext().packageName,
                )
            } catch (e: Resources.NotFoundException) {
                R.drawable.ic_none
            }
            val binding = LegendBinding().apply {
                color.value = line.color
                label.value = data.display.label
                icon.value = if (iconRes == R.drawable.ic_none || iconRes == 0) {
                    if (data.pid == null) R.drawable.ic_none else R.drawable.ic_box
                } else {
                    iconRes
                }
            }
            data.notifyUpdate = {
                notifyUpdate(it, binding)
            }
            binding
        }
        binding.layoutManager = StaggeredGridLayoutManager(legendBinding.size, RecyclerView.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }
        binding.legendData = LegendAdapter(ImmutableList.copyOf(legendBinding))
        graph.viewport.setMinX(Date().time - 22_000.0)
    }

    fun notifyUpdate(data: TorqueData, binding: LegendBinding) {
        val line = series[data]
        val now = Date()
        val scaled = (data.lastData / (data.display.maxValue - data.display.minValue)) * 100f
        line?.appendData(DataPoint(now, scaled), true, 100)
        if (data.hasReceivedNonZero)
            binding.value.value = "${data.lastDataStr}${data.display.unit}"
    }
}