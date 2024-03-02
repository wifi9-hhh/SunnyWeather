package com.sunnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.ui.weather.WeatherActivity
//import kotlinx.android.synthetic.main.fragment_place.*


class PlaceFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_place, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val searchPlaceEdit = view.findViewById<EditText>(R.id.searchPlaceEdit)
        val bgImageView = view.findViewById<ImageView>(R.id.bgImageView)

        // 设置RecyclerView的LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // 初始化Adapter
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter

        // 为EditText添加TextWatcher
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }// ...（原有TextWatcher的逻辑不变）
        }

        // 观察ViewModel的LiveData并在Observer中更新UI
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }// ...（原有的LiveData Observer逻辑不变）
        })

        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
//        val layoutManager = LinearLayoutManager(activity)
//
//        recyclerView.layoutManager = layoutManager
//        adapter = PlaceAdapter(this, viewModel.placeList)
//        recyclerView.adapter = adapter
//        searchPlaceEdit.addTextChangedListener { editable ->
//            val content = editable.toString()
//            if (content.isNotEmpty()) {
//                viewModel.searchPlaces(content)
//            } else {
//                recyclerView.visibility = View.GONE
//                bgImageView.visibility = View.VISIBLE
//                viewModel.placeList.clear()
//                adapter.notifyDataSetChanged()
//            }
//        }
//        viewModel.placeLiveData.observe(this, Observer{ result ->
//            val places = result.getOrNull()
//            if (places != null) {
//                recyclerView.visibility = View.VISIBLE
//                bgImageView.visibility = View.GONE
//                viewModel.placeList.clear()
//                viewModel.placeList.addAll(places)
//                adapter.notifyDataSetChanged()
//            } else {
//                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
//                result.exceptionOrNull()?.printStackTrace()
//            }
//        })
    }

}