package com.example.prakpm_2417052004

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prakpm_2417052004.ui.theme.PrakPM_2417052004Theme
import model.Food
import model.FoodSource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakPM_2417052004Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FoodScreen(foods = FoodSource.dummyFood,
                                modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun FoodScreen(foods: List<Food>, modifier: Modifier = Modifier){
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ){
        Text(
            text = "Selamat Datang, User!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Mau makan apa hari ini?",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(){
            foods.forEach{ food ->
                FoodCard(food)
            }
        }
    }
}

@Composable
fun FoodCard(food : Food){
    Column(){
        Text(text = "Tipe Makanan: ${food.nama}")
        Text(text = "Jenis: ${food.deskripsi}")
        Text(text = "Harga : Rp.${food.harga}")
        Image(
            painter = painterResource(id = food.imageRes),
            contentDescription = food.deskripsi,
            modifier = Modifier.size(72.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrakPM_2417052004Theme {

    }
}