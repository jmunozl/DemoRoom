package cl.jlaak.demoroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cl.jlaak.demoroom.databinding.ActivityMainBinding
import cl.jlaak.demoroom.db.Subscriber
import cl.jlaak.demoroom.db.SubscriberDatabase
import cl.jlaak.demoroom.db.SubscriberRepository


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = SubscriberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscriberViewModelFactory(repository)
        subscriberViewModel = ViewModelProvider(this, factory).get(SubscriberViewModel::class.java)
        binding.myViewModel = subscriberViewModel
        binding.lifecycleOwner = this
        initRecyclerView()
        subscriberViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun initRecyclerView() {
        binding.rvSubscriber.layoutManager = LinearLayoutManager(this)
        adapter =
            MyRecyclerViewAdapter({ selectedItem: Subscriber -> listItemClicked(selectedItem) })
        binding.rvSubscriber.adapter = adapter
        displaySubscriberList()
    }

    private fun displaySubscriberList() {
        subscriberViewModel.subscribers.observe(this, Observer {
            Log.i("MYTAG", it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()

        })
    }

    private fun listItemClicked(subscriber: Subscriber) {
        //Toast.makeText(this, "Nombre seleccionado is ${subscriber.name}", Toast.LENGTH_SHORT).show()
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }


}