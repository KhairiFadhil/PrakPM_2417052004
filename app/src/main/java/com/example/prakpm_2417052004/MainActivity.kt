package com.example.prakpm_2417052004
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prakpm_2417052004.ui.theme.PrakPM_2417052004Theme
import coil.compose.AsyncImage
import com.example.prakpm_2417052004.data.model.Food
import com.example.prakpm_2417052004.data.repository.FoodRepository
import model.ServiceItem
import model.ServiceSource
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import com.example.prakpm_2417052004.ui.theme.CardSecondary
import com.example.prakpm_2417052004.ui.theme.FavoriteActive
import com.example.prakpm_2417052004.ui.theme.TeksTipis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakPM_2417052004Theme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar()
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    FoodScreen(
                        services = ServiceSource.dummyServices,
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FoodScreen(
    services: List<ServiceItem>,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val repository = remember { FoodRepository() }
    var foods by remember { mutableStateOf<List<Food>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            withContext(Dispatchers.IO) { delay(800) }
            val result = repository.getFoods()
            foods = result
            isError = result.isEmpty()
            isLoading = false
            if (!isError) snackbarHostState.showSnackbar("Menu berhasil dimuat")
            else snackbarHostState.showSnackbar("Gagal memuat data: periksa koneksi")
        } catch (e: Exception) {
            isLoading = false
            isError = true
            snackbarHostState.showSnackbar("Gagal memuat data: periksa koneksi")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Selamat Datang, User!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Mau makan apa hari ini?",
            style = MaterialTheme.typography.bodyMedium,
            color = TeksTipis
        )
        Spacer(modifier = Modifier.height(20.dp))
        ServiceRow(services = services)
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Menu Pilihan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (isError || foods.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Gagal Memuat Data",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pastikan koneksi internet Anda menyala",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TeksTipis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(foods) { food ->
                    FoodCard(food)
                }
            }
        }
    }
}

@Composable
fun ServiceRow(services: List<ServiceItem>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(services) { service ->
            ServiceItemCard(service)
        }
    }
}

@Composable
fun ServiceItemCard(service: ServiceItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardSecondary),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = service.iconRes),
                contentDescription = service.label,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = service.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun BottomNavBar() {
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = TeksTipis,
        unselectedTextColor = TeksTipis,
        indicatorColor = Color.Transparent
    )

    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { selectedItem = 0 },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = navItemColors
        )

        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { selectedItem = 1 },
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorite") },
            label = { Text("Favorite") },
            colors = navItemColors
        )

        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = { selectedItem = 2 },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = navItemColors
        )
    }
}
@Composable
fun FoodCard(food: Food) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = food.deskripsi,
                placeholder = painterResource(id = R.drawable.sate_ayam),
                error = painterResource(id = R.drawable.sate_padang),
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = food.deskripsi,
                    style = MaterialTheme.typography.bodySmall,
                    color = TeksTipis,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Rp ${String.format("%,d", food.harga)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { isFavorite = !isFavorite }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite
                                  else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) FavoriteActive else TeksTipis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrakPM_2417052004Theme {

    }
}