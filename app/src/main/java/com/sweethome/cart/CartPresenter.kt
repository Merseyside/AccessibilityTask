package com.sweethome.cart

import com.sweethome.CartRepository
import com.sweethome.MockLoader
import com.sweethome.RootRouter
import com.sweethome.base.MockPresenter
import com.sweethome.item.FullItemViewModel

class CartPresenter(
    private val rootRouter: RootRouter,
    mockLoader: MockLoader,
    private val cartRepository: CartRepository
): MockPresenter<CartMvpView>(mockLoader) {

    override fun attach(mvpView: CartMvpView) {
        super.attach(mvpView)
        val items = loadData()

        if (items.isEmpty()) {
            mvpView.showEmptyCart()
        } else {
            val fullPrice = items.keys.sumBy { it.price.toInt() }.toString()
            mvpView.onDataLoaded(
                CartModel(
                    items.keys.toList(),
                    "₽255",
                    items.values.sum().toString(),
                    "₽${fullPrice}"
                )
            )
        }
    }

    fun loadData(): Map<FullItemViewModel, Int> {
        val fullItemsList = mockLoader.loadCatalog()
        val cartItems = fullItemsList.filter {
            cartRepository.cartItems.keys.contains(it.id)
        }

        return cartItems.map {
            it to (cartRepository.cartItems[it.id] ?: 0)
        }.toMap()
    }

    fun onCheckoutClick() {
        rootRouter.openCheckout(loadData().keys.sumBy { it.price.toInt() }.toString())
    }


}