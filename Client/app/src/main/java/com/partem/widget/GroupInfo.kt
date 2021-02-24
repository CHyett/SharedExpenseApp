package com.partem.widget

/**
 * Used for holding information regarding the group.
 */
class GroupInfo {
    var animating = false
    var expanding = false
    var firstChildPosition = 0

    /**
     * This variable contains the last known height value of the dummy view.
     * We save this information so that if the user collapses a group
     * before it fully expands, the collapse animation will start from the
     * CURRENT height of the dummy view and not from the full expanded
     * height.
     */
    var dummyHeight = -1
}