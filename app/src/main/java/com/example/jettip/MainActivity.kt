package com.example.jettip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettip.Components.InputField
import com.example.jettip.ui.theme.JetTipTheme
import com.example.jettip.util.calculateTotalPerPerson
import com.example.jettip.util.calculateTotalTip
import com.example.jettip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MyApp {
                    Column() {
//                        TopHeader()
                        MainContent()
                    }
                }
        }
    }
}

@Composable
fun MyApp(content:@Composable ()->Unit) {
    JetTipTheme {
    Surface(color = MaterialTheme.colors.background) {
        content()
    }
}
}

//@Preview
@Composable
fun TopHeader(totalPerPerson:Double=134.0){
Surface(modifier = Modifier
    .padding(15.dp)
    .fillMaxWidth()
    .height(150.dp)
    .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))), color = Color(0XFFE9D7F7)
) {
   Column(modifier = Modifier.padding(8.dp),
       verticalArrangement = Arrangement.Center,
       horizontalAlignment = Alignment.CenterHorizontally ) {
       val total = "%.2f".format(totalPerPerson)
        Text(text = "Total per person", style = MaterialTheme.typography.h5)
       Text(text = "$$total",style = MaterialTheme.typography.h4, fontWeight = FontWeight.ExtraBold)
   }
}
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent(){
    val splitByState = remember {
        mutableStateOf(1)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val range = IntRange(start = 1, endInclusive = 100)

    BillForm(
    splitByState = splitByState,
    totalPerPersonState = totalPerPersonState,
    tipAmountState = tipAmountState){}
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(modifier: Modifier=Modifier,
             rang:IntRange=1..100,
             splitByState:MutableState<Int>,
             tipAmountState:MutableState<Double>,
             totalPerPersonState:MutableState<Double>,
onValChange:(String)->Unit={}
             ){

//    state
    val totalBillState= remember {
        mutableStateOf("")
    }
    val validState= remember(totalBillState.value) {
        totalBillState.value.trim()
            .isNotEmpty()}
    val keyboardController=LocalSoftwareKeyboardController.current
    val sliderPostionState= remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPostionState.value*100).toInt()


//    top header
    TopHeader(totalPerPerson = totalPerPersonState.value)


    Surface(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray
        )) {
        Column(
            modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled =true ,
                isSingleLine =true,
                onAction = KeyboardActions{
                    if (!validState)return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                })
            
            if (validState){
                Row(modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start ) {
                    Text(text = "Split",
                        modifier = Modifier.align(alignment =Alignment.CenterVertically ))
                    Spacer(modifier = Modifier.width(120.dp))

                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End)
                    {
                            RoundIconButton(
                                  imageVector = Icons.Default.Remove, onClick = {

                                      splitByState.value=
                                          if (splitByState.value>1)splitByState.value-1 else 1

                                    totalPerPersonState.value=
                                        calculateTotalPerPerson(
                                            totalBill = totalBillState.value.toDouble(),
                                            splitBy = splitByState.value,
                                            tipPercentage=tipPercentage)

                                     })
                        Text(text = "${splitByState.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if(splitByState.value<rang.last){
                                    splitByState.value=splitByState.value+1
                                    totalPerPersonState.value=
                                        calculateTotalPerPerson(
                                            totalBill = totalBillState.value.toDouble(),
                                            splitBy = splitByState.value,
                                            tipPercentage=tipPercentage)
                                }

                            })
                    }
                }

//            Tip row
            Row (modifier = Modifier.padding(horizontal = 3.dp
            , vertical = 12.dp)){
              Text(text = "Tip",
              modifier = Modifier.align(alignment = Alignment.CenterVertically)
                  ) 
                Spacer(modifier = Modifier.width(200.dp))
                Text(text = "$ ${tipAmountState.value}")
            }
            
            Column(verticalArrangement = Arrangement.Center, 
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$tipPercentage %")
                Spacer(modifier = Modifier.height(14.dp))
//                Slider
                Slider(value = sliderPostionState.value,
                    onValueChange = {newVal->
                        sliderPostionState.value=newVal
                        tipAmountState.value=
                             calculateTotalTip(
                                 totalBill=totalBillState.value.toDouble(),
                                 tipPercentage=tipPercentage

                             )
                                totalPerPersonState.value=
                                    calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage=tipPercentage)
                    },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    steps = 5, onValueChangeFinished = {

                    })
            }
               
            }else{
                Box(modifier = Modifier){

                }
            }

//        end of column
        }
    }

}




