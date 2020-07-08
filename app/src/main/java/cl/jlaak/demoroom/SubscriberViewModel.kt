package cl.jlaak.demoroom

import android.util.Patterns
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.jlaak.demoroom.db.Subscriber
import cl.jlaak.demoroom.db.SubscriberRepository
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel(), Observable {

    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    @Bindable
    val inputName = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    @Bindable
    val btnSaveOrUpdate = MutableLiveData<String>()

    @Bindable
    val btnClearOrDelete = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage


    init {
        btnSaveOrUpdate.value = "Save"
        btnClearOrDelete.value = "Clear"
    }

    fun saveOrUpdate() {

        if (inputName.value == null) {
            statusMessage.value = Event("Please enter name")
        } else if (inputEmail.value == null) {
            statusMessage.value = Event("Please enter email")
            /*} else if (Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
                statusMessage.value = Event("Please enter email correct")*/
        } else {
            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!

                insert(Subscriber(0, name, email))
                inputName.value = null
                inputEmail.value = null
            }

        }

    }

    fun clearOrDelete() {
        clearAll()
    }

    fun insert(subscriber: Subscriber) = viewModelScope.launch {
        val newRowid = repository.insert(subscriber)
        if (newRowid > -1) {
            statusMessage.value = Event("Subscriber Inserted Successfully $newRowid")
        } else {
            statusMessage.value = Event("Subscriber Inserted Error")
        }


    }

    fun update(subscriber: Subscriber) = viewModelScope.launch {
        val updateRow = repository.update(subscriber)
        if (updateRow > 0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            btnSaveOrUpdate.value = "Save"
            btnClearOrDelete.value = "Clear All"
            statusMessage.value = Event("Subscriber Updated Successfully")
        } else {
            statusMessage.value = Event("Subscriber Updated Error")
        }
    }

    fun delete(subscriber: Subscriber) = viewModelScope.launch {
        val deleteRow = repository.delete(subscriber)
        if (deleteRow > 0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            btnSaveOrUpdate.value = "Save"
            btnClearOrDelete.value = "Clear All"
            statusMessage.value = Event("Subscriber Deleted Successfully")
        } else {
            statusMessage.value = Event("Subscriber Deleted Error")
        }

    }

    fun clearAll() = viewModelScope.launch {
        val deleteAllRow = repository.deleteAll()
        if (deleteAllRow > 0) {
            statusMessage.value = Event("$deleteAllRow  Row Deleted Successfully")
        } else {
            statusMessage.value = Event("All Subscriber Clear Error")
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            delete(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        btnSaveOrUpdate.value = "Update"
        btnClearOrDelete.value = "Delete"
    }


}