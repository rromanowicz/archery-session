package ex.rr.archerysession

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.Position
import com.rr.archerysession.R
import com.rr.archerysession.databinding.FragmentStatisticsDialogBinding
import ex.rr.archerysession.data.DbSession
import ex.rr.archerysession.data.Session
import ex.rr.archerysession.db.DBHelper


class StatisticsDialogFragment(
    session: Session?,
    isSession: Boolean = true
) :
    DialogFragment() {

    private var session: Session? = null
    private var isSession: Boolean? = null

    init {
        this.session = session
        this.isSession = isSession
    }

    companion object {
        const val TAG = "SessionDetailsFragment"
    }

    private lateinit var binding: FragmentStatisticsDialogBinding

    private lateinit var sharedViewModel: SharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentStatisticsDialogBinding.inflate(layoutInflater)

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.setGravity(Gravity.TOP)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        if (session != null && isSession!!) {
            drawSessionCharts(session!!)
            Log.d(this::class.java.name, "Loaded session statistics.")
        } else if (session == null && !isSession!!) {
            drawTotalCharts()
            Log.d(this::class.java.name, "Loaded total statistics.")
        } else {
            binding.statsScrollView.children.forEach {
                it.visibility = View.GONE
            }
        }
    }

    private fun drawTotalCharts() {
        val db = DBHelper(requireContext(), null)
        val lastXSessions = db.getXSessions("10")

        val combinedScores: MutableList<MutableList<Int>> = mutableListOf()
        lastXSessions.forEach {
            combinedScores.addAll(it.session.scores)
        }
        val combinedArrows: Int = combinedScores.sumOf { it.size }

        drawBarChart(combinedArrows, getScoreOccurrence(combinedScores))
        drawPieChart(combinedArrows, getScoreOccurrence(combinedScores))
        drawLineChart2(lastXSessions)

    }


    private fun drawSessionCharts(session: Session) {
        drawBarChart(session.arrows, getScoreOccurrence(session.scores))
        drawPieChart(session.arrows, getScoreOccurrence(session.scores))
        drawLineChart(session.scores)
    }

    private fun drawLineChart(scores: MutableList<MutableList<Int>>) {
        val anyChartView: AnyChartView = binding.lineChart
        APIlib.getInstance().setActiveAnyChartView(anyChartView)

        val chart = AnyChart.line()
        val data: MutableList<DataEntry> = ArrayList()

        var counter = 1
        scores.forEach { score ->
            data.add(ValueDataEntry(counter, score.sumOf { it }))
            counter++
        }
        chart.data(data)
        chart.xAxis(0).title(getString(R.string.ends))
        chart.yAxis(0).title(getString(R.string.score))
        chart.background("#000")

        anyChartView.setChart(chart)
    }

    private fun drawLineChart2(sessions: MutableList<DbSession>) {
        val anyChartView: AnyChartView = binding.lineChart
        APIlib.getInstance().setActiveAnyChartView(anyChartView)

        val chart = AnyChart.line()

        sessions.forEach {
            val set = Set.instantiate()
            val seriesMapping: Mapping = set.mapAs("{ x: 'x', value: 'value' }")
            val line: Line = chart.line(seriesMapping)
            line.hovered().markers().enabled(true)
            line.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4.0)
            line.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5.0)
                .offsetY(5.0)
            val data: MutableList<DataEntry> = ArrayList()
            chart.addSeries(set)
            line.name(it.session.startDate.toString())
            getEndAvgs(it.session.scores).forEach { (key, value) ->
                data.add(ValueDataEntry(key, value))
            }
            line.data(data)
            chart.addSeries(set)
        }

        chart.xAxis(0).title(getString(R.string.ends))
        chart.yAxis(0).title(getString(R.string.score))
        chart.background("#000")
        chart.legend().enabled(false)

        anyChartView.setChart(chart)
    }

    private fun drawPieChart(totalShots: Int, scoreMap: Map<Int, Int>) {
        val anyChartView: AnyChartView = binding.pieChart
        APIlib.getInstance().setActiveAnyChartView(anyChartView)

        val chart = AnyChart.pie()
        val data: MutableList<DataEntry> = ArrayList()

        scoreMap.keys.forEach {
            data.add(ValueDataEntry(it, scoreMap[it]!!.div(totalShots.toDouble()) * 100))
        }
        chart.data(data)
        chart.background("#000")
        chart.tooltip()
            .titleFormat("{%X}")
            .position(Position.CENTER_BOTTOM)
            .anchor(Anchor.CENTER_BOTTOM)
            .offsetX(0.0)
            .offsetY(5.0)
            .format("{%Value}{numDecimals:1}%")
        chart.legend().enabled(false)

        anyChartView.setChart(chart)
    }

    private fun drawBarChart(totalShots: Int, scoreMap: Map<Int, Int>) {
        val anyChartView: AnyChartView = binding.barChart
        APIlib.getInstance().setActiveAnyChartView(anyChartView)

        val chart = AnyChart.bar()

        val data: MutableList<DataEntry> = ArrayList()
        scoreMap.keys.forEach {
            data.add(ValueDataEntry(it, scoreMap[it]!!.div(totalShots.toDouble()) * 100))
        }
        chart.data(data)

        chart.tooltip()
            .titleFormat("{%X}")
            .position(Position.CENTER_BOTTOM)
            .anchor(Anchor.CENTER_BOTTOM)
            .offsetX(0.0)
            .offsetY(5.0)
            .format("{%Value}{numDecimals:1}%")

        chart.xAxis(0).title(getString(R.string.score))
        chart.yAxis(0).title(getString(R.string.percent))
        chart.background("#000")

        anyChartView.setChart(chart)
    }

    private fun getScoreOccurrence(scores: MutableList<MutableList<Int>>): Map<Int, Int> {
        val scoreList: MutableList<Int> = mutableListOf()
        scores.forEach(scoreList::addAll)
        return scoreList.groupingBy { it }.eachCount().toSortedMap()
    }

    private fun getEndAvgs(scores: MutableList<MutableList<Int>>): Map<Number, Number> {
        var counter = 1
        val mMap: MutableMap<Int, Int> = mutableMapOf()

        scores.forEach { sc ->
            mMap[counter] = sc.sumOf { it }.div(sc.size)
            counter++
        }
        return mMap.toMap()
    }

}