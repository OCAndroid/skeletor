package skeletor.sample.utils

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import skeletor.sample.list.JourneyListFragmentDirections
import skeletor.sample.model.Journey

fun Fragment.navigateToJourneyDetail(journey: Journey) {
    val action = JourneyListFragmentDirections.actionJourneyListToDetail(journey)
    findNavController().navigate(action)
}

