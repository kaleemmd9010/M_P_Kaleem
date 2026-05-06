package com.example.shopapp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add

import androidx.compose.material.icons.filled.ShoppingCart

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shopapp.ui.theme.ShopAppTheme
import java.text.NumberFormat
import java.util.Locale


data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val imageRes: Int,
    val iconBg: Color
)


class ShopViewModel : ViewModel() {
    val products = listOf(
        Product(1, "Wireless Headphones", 79.99, "Noise cancelling over-ear headphones", R.drawable.wireless_headphones, Color(0xFFE3F2FD)),
        Product(2, "Smart Watch", 199.99, "Fitness tracking and notifications", R.drawable.smartwatch, Color(0xFFFFF3E0)),
        Product(3, "USB-C Cable", 12.99, "Fast charging 2m braided cable", R.drawable.usb_c_type_cable, Color(0xFFE8F5E9)),
        Product(4, "Bluetooth Speaker", 49.99, "Portable waterproof speaker", R.drawable.bluetoothspeaker, Color(0xFFF3E5F5)),
        Product(5, "Phone Case", 19.99, "Shockproof protective case", R.drawable.phonecase, Color(0xFFE0F7FA)),
        Product(6, "Laptop Stand", 34.99, "Aluminum adjustable stand", R.drawable.laptop_stand, Color(0xFFFFFDE7)),
        Product(7, "Wireless Mouse", 24.99, "Ergonomic silent click mouse", R.drawable.wireless_mouse, Color(0xFFFBE9E7)),
        Product(8, "Webcam 4K", 89.99, "Auto-focus with microphone", R.drawable.webcam_4k, Color(0xFFE8EAF6)),
    )

    private val _cart = mutableStateMapOf<Product, Int>()
    val cart: Map<Product, Int> = _cart

    val cartTotal: Double
        get() = _cart.entries.sumOf { it.key.price * it.value }

    val cartItemCount: Int
        get() = _cart.values.sum()

    fun addToCart(product: Product) {
        _cart[product] = (_cart[product] ?: 0) + 1
    }

    fun removeFromCart(product: Product) {
        val current = _cart[product] ?: 0
        if (current <= 1) {
            _cart.remove(product)
        } else {
            _cart[product] = current - 1
        }
    }

    fun clearCart() {
        _cart.clear()
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopAppTheme {
                ShopApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopApp(viewModel: ShopViewModel = viewModel()) {
    var showCart by remember { mutableStateOf(false) }


    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF8F9FB),
            Color(0xFFE9ECEF),
            Color(0xFFDEE2E6)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (showCart) "Cart" else "Gadgets Shop") },
                    navigationIcon = {
                        if (showCart) {
                            IconButton(onClick = { showCart = false }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        if (!showCart) {
                            BadgedBox(
                                badge = {
                                    if (viewModel.cartItemCount > 0) {
                                        Badge { Text(viewModel.cartItemCount.toString()) }
                                    }
                                }
                            ) {
                                IconButton(onClick = { showCart = true }) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6C63FF).copy(alpha = 0.9f),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            if (showCart) {
                CartScreen(
                    cart = viewModel.cart,
                    total = viewModel.cartTotal,
                    onRemove = { viewModel.removeFromCart(it) },
                    onAdd = { viewModel.addToCart(it) },
                    onClear = { viewModel.clearCart() },
                    modifier = Modifier.padding(padding)
                )
            } else {
                ProductListScreen(
                    products = viewModel.products,
                    onAddToCart = { viewModel.addToCart(it) },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun ProductListScreen(
    products: List<Product>,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(product = product, onAdd = { onAddToCart(product) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(product: Product, onAdd: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = product.iconBg,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatPrice(product.price),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF6C63FF),
                    fontWeight = FontWeight.ExtraBold
                )
            }


            Surface(
                onClick = onAdd,
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF6C63FF),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "+",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    cart: Map<Product, Int>,
    total: Double,
    onRemove: (Product) -> Unit,
    onAdd: (Product) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (cart.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Oops!Empty Cart",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Blue
                )
            }
        }
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cart.entries.toList(), key = { it.key.id }) { entry ->
                CartItemCard(
                    product = entry.key,
                    quantity = entry.value,
                    onRemove = { onRemove(entry.key) },
                    onAdd = { onAdd(entry.key) }
                )
            }
        }

        Surface(
            tonalElevation = 3.dp,
            shadowElevation = 12.dp,
            color = Color.White,
            modifier = Modifier.background(Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.DarkGray
                    )
                    Text(
                        formatPrice(total),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF6C63FF),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onClear,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6584)
                    )
                ) {
                    Text("Clear Cart", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}



@Composable
fun CartItemCard(
    product: Product,
    quantity: Int,
    onRemove: () -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = product.iconBg,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${formatPrice(product.price)} × $quantity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedIconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("−", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = quantity.toString(),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                FilledIconButton(
                    onClick = onAdd,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}
fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(price)
}