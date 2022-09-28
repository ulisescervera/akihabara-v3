package com.gmail.uli153.akihabara3.ui.products

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.daimajia.swipe.SwipeLayout
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.FragmentProductsBaseBinding
import com.gmail.uli153.akihabara3.domain.models.Product
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.utils.SnackBarManager
import com.gmail.uli153.akihabara3.utils.setProductImage
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.row_product.view.*
import kotlinx.coroutines.*

interface ProductListener {
    fun onBuyProduct(Product: Product)
    fun onFavoriteProduct(Product: Product)
    fun onEditProduct(Product: Product)
}

abstract class ProductsBaseFragment : AkbFragment(), ProductListener {

    private var _binding: FragmentProductsBaseBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    protected val productsViewModel: ProductsViewModel by activityViewModels()

    protected var products: List<Product> = listOf()
        set(value) {
            field = value
            filter()
        }

    private var filteredProductEntities: List<Product> = listOf()
        set(value) {
            field = value
            adapter.submitList(value)
        }

    protected val adapter: ProductAdapter by lazy {
        ProductAdapter(this).apply {
            registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    smoothScroller.targetPosition = Math.max(0, positionStart - 1)
                    layoutManager.startSmoothScroll(smoothScroller)
                }
            })
        }
    }

    protected val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    protected val smoothScroller: LinearSmoothScroller by lazy {
        object: LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }
    }

    private val snackBarManager: SnackBarManager by lazy {
        SnackBarManager(requireContext(), binding.root, lifecycleScope)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductsBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val separator = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL).also {
            it.setDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.separator_product)!!)
        }
        binding.recyclerviewProducts.layoutManager = layoutManager
        binding.recyclerviewProducts.addItemDecoration(separator)
        binding.recyclerviewProducts.adapter = adapter
    }

    override fun onBuyProduct(Product: Product) {
        productsViewModel.buyProduct(Product) { transactionId ->
            val message = getString(R.string.snackbar_undo_message, Product.name, AkbNumberParser.LocaleParser.format(Product.price))
            snackBarManager.showUndoSnackbar(message) {
                productsViewModel.deleteTransaction(transactionId)
            }
        }
    }

    override fun onFavoriteProduct(Product: Product) {
        productsViewModel.toggleFavorite(Product)
    }

    override fun onEditProduct(Product: Product) {
        val dirs = ProductsFragmentDirections.actionEditProduct(Product.id)
        navigate(dirs)
    }

    protected fun filter() {
        //todo filter
        filteredProductEntities = products
    }

    protected inner class ProductVH(
        view: View,
        private val context: Context,
        private val listener: ProductListener
    ) : RecyclerView.ViewHolder(view) {

        private var toggleFavoriteJob: Job? = null

        init {
            itemView.btn_buy.setSafeClickListener {
                itemView.swipe.close(true)
                listener.onBuyProduct(Product)
            }
            itemView.btn_favorite.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    onToggleLike(Product, true)
                }
                override fun unLiked(likeButton: LikeButton?) {
                    onToggleLike(Product, false)
                }
            })
            itemView.btn_favorite.setOnAnimationEndListener {
                // Solo cuando like se pone a true se lanza este callback
                toggleFavoriteJob?.start()
            }
            itemView.btn_edit.setSafeClickListener {
                listener.onEditProduct(Product)
            }
            itemView.surface.setSafeClickListener {
                when (itemView.swipe.openStatus) {
                    SwipeLayout.Status.Open -> itemView.swipe.close(true)
                    SwipeLayout.Status.Close -> itemView.swipe.open(true)
                }
            }
        }

        lateinit var Product: Product private set
        fun setup(Product: Product) {
            this.Product = Product
            itemView.swipe.dragDistance = 120
            itemView.swipe.close(false)
            itemView.imageview_product.setProductImage(Product)
            itemView.label_name.text = Product.name
            itemView.label_price.text = String.format("%s €", AkbNumberParser.LocaleParser.format(Product.price))
            itemView.btn_favorite.isLiked = Product.favorite
        }

        private fun onToggleLike(Product: Product, lazy: Boolean) {
            val start = if (lazy) CoroutineStart.LAZY else CoroutineStart.DEFAULT
            toggleFavoriteJob = lifecycleScope.launch(Dispatchers.Main, start = start) {
                if (!lazy) delay(100)
                listener.onFavoriteProduct(Product)
            }
        }
    }

    protected inner class ProductDiffCallback: DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    protected inner class ProductAdapter(
        private val listener: ProductListener
    ) : ListAdapter<Product, ProductVH>(ProductDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.row_product, parent, false)
            return ProductVH(view, requireContext(), listener)
        }

        override fun onBindViewHolder(holder: ProductVH, position: Int) {
            holder.setup(getItem(position))
        }
    }
}
