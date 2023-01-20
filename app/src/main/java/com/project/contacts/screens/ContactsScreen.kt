package com.project.contacts.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.project.contacts.R
import com.project.contacts.model.ContactsViewModel
import com.project.contacts.model.ContactNavigationViewModel
import com.project.contacts.model.UserAction
import java.util.*


@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel,
    sharedContactViewModel: ContactNavigationViewModel,
    navController: NavHostController?
)
{

    val state = viewModel.state
    val list= viewModel.contactsStateList.distinctBy { it.displayName }

    Scaffold(
        topBar =
        {
            if (state.isSearchBarVisible)
                SearchAppBar(
                    onCloseClicked = {
                        viewModel.onAction(UserAction.CloseIconClicked)
                    },
                    onTextChange = { text ->
                        viewModel.onAction(UserAction.TextFieldInput(text))
                    },
                    onSearchClicked = {},
                    searchText = state.searchText
                )
            else
                TopAppBar(onSearchIconClick = {
                    viewModel.onAction(UserAction.SearchIconClicked)
                })
        }, modifier = Modifier.fillMaxSize())
    {
        Surface{
            LazyColumn(verticalArrangement = Arrangement.SpaceEvenly) {
                items(list.distinct()) { contact ->
                    ContactItem(
                        contactPicture = contact.photo,
                        contactName = if(
                            contact.firstName.isNotEmpty() && contact.lastName.isNotEmpty())
                            contact.firstName+" "+contact.lastName else contact.displayName
                    ) {
                        sharedContactViewModel.updateContact(contact)
                        navController?.navigate("contact_details")
                    }

                }
            }
        }
    }
}

@Composable
fun TopAppBar(onSearchIconClick: () -> Unit) {
    TopAppBar(title = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = LocalContext.current.resources.getString
                    (R.string.app_name)
            )
            IconButton(onClick = onSearchIconClick) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search Icon",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
    )
}



@Composable
fun SearchAppBar(
    searchText: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = MaterialTheme.colors.primary
    ) {
        TextField(modifier = Modifier
            .fillMaxWidth(),
            value = searchText,
            onValueChange = {
                onTextChange(it)
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    text = "Search...",
                    color = Color.White
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.subtitle1.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.White
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (searchText.isNotEmpty()) {
                            onTextChange("")
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        tint = Color.White
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(searchText)
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
            ))
    }
}

@Composable
fun ContactPicture(contactName: String,
                   contactPicture: String,
                   cardSize: Dp,
                   fontSize: TextUnit,
                   photoSize: Dp
){
    val random= Random()
    val randomColor=  Color(
        255, random.nextInt(256), random.nextInt(256), random.nextInt(256)
    )
    if (contactPicture.isEmpty()){
        Box()
        {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(16.dp)
                    .size(cardSize),
                backgroundColor = randomColor
            ){}
            Text(
                text = contactName[0].toString(),
                modifier = Modifier.align(Alignment.Center),
                color = Color.Red,
                fontSize = fontSize,
            )
        }
    }
    else
        Image(
            painter = rememberAsyncImagePainter(contactPicture),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(16.dp)
                .size(photoSize)
                .clip(CircleShape)
        )
}

@Composable
fun ContactItem(contactPicture: String, contactName: String, clickAction: () -> Unit)
{

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clickable(onClick = { clickAction.invoke() })
    )
    {
        Row(/*modifier= Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly*/)
        {
            ContactPicture(
                contactName = contactName,
                contactPicture =contactPicture ,
                cardSize = 60.dp,
                fontSize = 30.sp,
                photoSize = 60.dp
            )

            Text(
                text = contactName,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
            )

        }

    }
}