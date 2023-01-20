package com.project.contacts

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.project.contacts.model.ContactsViewModel
import com.project.contacts.model.ContactNavigationViewModel
import com.project.contacts.screens.ContactDetails
import com.project.contacts.screens.ContactsScreen
import com.project.contacts.screens.EditContact


class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val multiplePermissionsState = rememberMultiplePermissionsState(
                listOf(
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.WRITE_CONTACTS
                )
            )

            CheckForPermissions(multiplePermissionsState)

        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CheckForPermissions(multiplePermissionsState: MultiplePermissionsState) {

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if(event == Lifecycle.Event.ON_START) {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }

            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )



    if (multiplePermissionsState.allPermissionsGranted){
        val viewModel= viewModel<ContactsViewModel>()
        val sharedContactViewModel: ContactNavigationViewModel= viewModel()
        ContactsApp(viewModel,sharedContactViewModel)
    }

    if(multiplePermissionsState.permissions.any { perm-> perm. shouldShowRationale})
        Toast.makeText(
            LocalContext.current,
            "You have to enable permissions to use the app",
            Toast.LENGTH_SHORT).show()

}


@Composable
fun ContactsApp(viewModel: ContactsViewModel, sharedContactViewModel: ContactNavigationViewModel)
{
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "contacts_list") {
        composable("contacts_list") {
            ContactsScreen(viewModel,sharedContactViewModel,navController)
        }
        composable(route = "contact_details") {
            ContactDetails(navController, sharedContactViewModel)
        }
        composable(route = "edit_contact") {
            EditContact(sharedContactViewModel, viewModel,navController)
        }

    }
}

