package com.example.funnytimesvendorapp.MainMenuSection.HomeSection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.funnytimesvendorapp.databinding.FtpFragHomeBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate


class HomeFrag: Fragment() {
    private var _binding: FtpFragHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FtpFragHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.SalesChart.setDrawBarShadow(false)
        binding.SalesChart.setDrawValueAboveBar(true)
        binding.SalesChart.setMaxVisibleValueCount(50)





        val weekdays = arrayOf(
            "Sun",
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat"
        ) // Your List / array with String Values For X-axis Labels

        val xAxis: XAxis = binding.SalesChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(weekdays)
        val values: ArrayList<BarEntry> = ArrayList()


            values.add(BarEntry(0.0f, 10.0f))
            values.add(BarEntry(1.0f, 20.0f))
            values.add(BarEntry(2.0f, 30.0f))
            values.add(BarEntry(3.0f, 40.0f))
            values.add(BarEntry(4.0f, 50.0f))
            values.add(BarEntry(5.0f, 60.0f))
            values.add(BarEntry(6.0f, 70.0f))
            values.add(BarEntry(7.0f, 80.0f))
            values.add(BarEntry(8.0f, 90.0f))
            values.add(BarEntry(9.0f, 50.0f))
            values.add(BarEntry(10.0f, 40.0f))


        val set1: BarDataSet

        if (binding.SalesChart.data != null &&
            binding.SalesChart.data.dataSetCount > 0
        ) {
            set1 = binding.SalesChart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            binding.SalesChart.data.notifyDataChanged()
            binding.SalesChart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "Data Set")
            set1.setColors(*ColorTemplate.VORDIPLOM_COLORS)
            set1.setDrawValues(false)
            val dataSets: ArrayList<IBarDataSet> = ArrayList()
            dataSets.add(set1)
            val data = BarData(dataSets)
            binding.SalesChart.data = data
            binding.SalesChart.setFitBars(true)
        }

        binding.SalesChart.invalidate()




        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}