package com.example.ecommerceproject.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ecommerceproject.Model.CategoryModel
import com.example.ecommerceproject.Model.ItemsModel
import com.example.ecommerceproject.Model.SliderModel
import com.example.ecommerceproject.R
import com.example.ecommerceproject.ViewModel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainActivityScreen{
                startActivity(Intent(this,SignInActivity::class.java))
            }
        }
    }
}

fun getUserLocalStorage(context: Context): HashMap<String, String> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    return hashMapOf(
        "uid" to (sharedPreferences.getString("uid", "") ?: ""),
        "first_name" to (sharedPreferences.getString("first_name", "") ?: ""),
        "last_name" to (sharedPreferences.getString("last_name", "") ?: ""),
        "email" to (sharedPreferences.getString("email", "") ?: "")
    )
}


@Composable
fun MainActivityScreen(onCartClick:()->Unit) {
    var ctxt = LocalContext.current
    val viewModel = MainViewModel()

    val banners = remember { mutableListOf<SliderModel>() }
    val categories = remember { mutableListOf<CategoryModel>() }
    val Popular = remember { mutableListOf<ItemsModel>() }


    var userData = getUserLocalStorage(ctxt);
    var showBannerLoading by remember { mutableStateOf(true) }
    var showCategoryLoading by remember { mutableStateOf(true) }
    var showPopularLoading by remember { mutableStateOf(true) }


    //banner
    LaunchedEffect(Unit) {
        viewModel.loadBanner().observeForever() {
            banners.clear()
            banners.addAll(it)
            showBannerLoading = false
        }
    }

    //category
    LaunchedEffect(Unit) {
        viewModel.loadCategory().observeForever() {
            categories.clear()
            categories.addAll(it)
            showCategoryLoading = false
        }
    }

    //Popular
    LaunchedEffect(Unit) {
        viewModel.loadPopular().observeForever() {
            Popular.clear()
            Popular.addAll(it)
            showPopularLoading = false
        }
    }

    ConstraintLayout (modifier = Modifier.background(Color.White)){
        val(scrollList,bottomMenu)=createRefs()
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(scrollList) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
        ){
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 70.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ){
                Column {
                    Text("Welcome Back", color = Color.Black)
                    Text(""+userData["first_name"],
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                    Row{
                        Image(
                            painter = painterResource(R.drawable.search_icon),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Image(
                            painter = painterResource(R.drawable.bell_icon),
                            contentDescription = null
                        )
                    }
                }
            }

            //banners
            item {
                if (showBannerLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Banners(banners)
                }
            }

            item {
                Text(
                    text = "Official Brand",
                    color = Color.Black,
                    fontSize =18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .padding(horizontal = 16.dp)
                )
            }
            item {
                if (showCategoryLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    CategoryList(categories)
                }
            }
            item {
                SectionTitle("Most Popular","See all")
            }
            item {
                if (showPopularLoading){
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                } else {
                    ListItems(Popular)
                }
            }
        }
        BottomMenu(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomMenu) {
                    bottom.linkTo(parent.bottom)
                },
            onItemClick = onCartClick
        )
    }
}

@Composable
fun CategoryList(categories: List<CategoryModel>) {
    var selectedIndex by remember { mutableStateOf(-1) }
    var context = LocalContext.current

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
    ) {
        items(categories.size) { index ->
            CategoryItem(
                item = categories[index],
                isSelected = selectedIndex == index,
                onItemClick = {
                    selectedIndex = index
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent= Intent(context,ListItemsActivity::class.java).apply{
                            putExtra("id",categories[index].id.toString())
                            putExtra("title",categories[index].title)
                        }
                        startActivity(context,intent,null)
                    }, 500)
                }
            )
        }
    }
}

@Composable
fun CategoryItem(item: CategoryModel, isSelected: Boolean, onItemClick: () -> Unit) {

    Column (
        modifier = Modifier
            .clickable (onClick = onItemClick), horizontalAlignment = Alignment.CenterHorizontally
    ){
        AsyncImage(
            model = (item.picUrl),
            contentDescription = item.title,
            modifier = Modifier
                .size(if (isSelected) 60.dp else 50.dp)
                .background(
                    color = if (isSelected) colorResource(R.color.darkBrown) else colorResource(R.color.lightBrown),
                    shape = RoundedCornerShape(100.dp)
                ),
            contentScale = ContentScale.Inside,
            colorFilter = if (isSelected){
                    ColorFilter.tint(Color.White)
            }else{
                ColorFilter.tint(Color.Black)
            }
        )
        Spacer(modifier = Modifier.padding(top=8.dp))
        Text(
            text = item.title,
            color = colorResource(R.color.darkBrown),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
fun SectionTitle(title: String, actionText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = title,
            color = Color.Black,
            fontSize = 18.sp,
        )
        Text(
            text = actionText,
            color = colorResource(R.color.darkBrown)
        )
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Banners(banners: List<SliderModel>) {
AutoSlidingCarousel(banners = banners)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingCarousel(
    modifier: Modifier = Modifier.padding(top=16.dp),
    pagerState: PagerState = remember {PagerState()},
    banners: List<SliderModel>

) {
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    Column (modifier=modifier.fillMaxSize()) {
        HorizontalPager(count = banners.size, state=pagerState) { page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(banners[page].url)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 8.dp)
                    .height(150.dp)
            )
        }
        DotIndicator(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterHorizontally),
            totalDots = banners.size,
            selectedIndex = if (isDragged) pagerState.currentPage else pagerState.currentPage,
            dotSize = 8.dp
        )
    }
}
@Composable
fun DotIndicator(
    modifier: Modifier=Modifier,
    totalDots:Int,
    selectedIndex: Int,
    selectedColor: Color= colorResource(R.color.darkBrown),
    unSelectedColor: Color = colorResource(R.color.grey),
    dotSize: Dp
) {
    LazyRow(
        modifier = modifier
            .wrapContentSize()
    ){
items(totalDots){index ->
    IndicatorDot(
        color = if(index==selectedIndex)selectedColor else unSelectedColor,
        size = dotSize
    )
            if (index!= totalDots-1){
        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}
@Composable
fun IndicatorDot(
    modifier: Modifier=Modifier,
    size: Dp,
    color: Color
){
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)

    )
}

@Composable
fun BottomMenu(modifier: Modifier,onItemClick: () -> Unit){
    var ctxt = LocalContext.current
    Row (modifier= modifier
        .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
        .background(
            colorResource(R.color.grey),
            shape = RoundedCornerShape(10.dp)
        ),
        horizontalArrangement = Arrangement.SpaceAround
    ){
        BottomMenuItem(icon = painterResource(R.drawable.btn_1), text = "Explorer")
        BottomMenuItem(icon = painterResource(R.drawable.btn_2), text = "Cart", onItemClick=onItemClick)
        BottomMenuItem(icon = painterResource(R.drawable.btn_3), text = "Favorite")
        BottomMenuItem(icon = painterResource(R.drawable.btn_4), text = "Orders")
        BottomMenuItem(icon = painterResource(R.drawable.btn_5), text = "Profile", onItemClick= { ctxt.startActivity(Intent(ctxt,ProfileActivity::class.java)) })
    }
}

@Composable
fun BottomMenuItem(icon:Painter, text:String,onItemClick:(()->Unit)?=null){
    Column (modifier = Modifier
        .height(70.dp)
        .clickable { onItemClick?.invoke() }
        .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        ){
        Icon(icon,contentDescription=text, tint=Color.White)
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(text, color = Color.White)
    }
}