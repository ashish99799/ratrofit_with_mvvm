package com.github.users.ui.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.users.model.api.ApiClient
import com.github.users.model.repositorys.MainActivityRepository
import com.github.users.model.responses.DataResponse
import com.github.users.model.responses.RowData
import com.github.users.utils.CheckInternetConnectionAvailable
import com.github.users.ui.listeners.MainActivityListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

// Override ViewModel
class MainActivityViewModel : ViewModel() {

    // Over Activity Listener
    var mainActivityListener: MainActivityListener? = null
    private var myCompositeDisposable: CompositeDisposable? = null
    var mainActivityRepository: MainActivityRepository

    init {
        mainActivityRepository = MainActivityRepository(ApiClient())
    }

    fun getSearchUser(context: Context, query: String, page: Int) {
        // Check Internet connectivity
        if (context.CheckInternetConnectionAvailable()) {
            // API Calling Start
            mainActivityListener?.showProgress()

            // Ratrofit API Calling
            myCompositeDisposable = CompositeDisposable()
            myCompositeDisposable?.add(
                mainActivityRepository.getSearchUser(query, page)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
            )
        } else {
            // Internet is not connected
            mainActivityListener?.hideProgress()
            mainActivityListener?.onFailure("Please check your internet connection!")
        }
    }

    private fun onResponse(response: DataResponse) {
        mainActivityListener?.hideProgress()
        mainActivityListener?.onSuccess(response.items!! as ArrayList<RowData>)
    }

    private fun onFailure(error: Throwable) {
        mainActivityListener?.hideProgress()
        mainActivityListener?.onFailure("Fail ${error.message}")
    }
}