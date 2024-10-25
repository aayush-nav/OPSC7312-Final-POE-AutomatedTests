package com.theateam.vitaflex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class WorkoutExpandableListAdapter(
    private val context: Context,
    private val groupList: List<String>, // Month names
    private val childMap: Map<String, List<Exercise>> // Map of month to exercises
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = groupList.size

    override fun getChildrenCount(groupPosition: Int): Int {
        return childMap[groupList[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): String = groupList[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Exercise? {
        return childMap[groupList[groupPosition]]?.get(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_log_group, parent, false)
        val groupText = view.findViewById<TextView>(R.id.group_name)
        groupText.text = groupList[groupPosition]
        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_log_child, parent, false)
        val child = getChild(groupPosition, childPosition)
        val exerciseText = view.findViewById<TextView>(R.id.child_name)
        exerciseText.text = "${child?.date}: ${child?.exerciseName} - ${child?.amount}"
        return view
    }
}