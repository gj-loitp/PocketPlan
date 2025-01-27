package com.pocket_plan.j7_003.data.sleepreminder

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pocket_plan.j7_003.MainActivity
import com.pocket_plan.j7_003.R
import com.pocket_plan.j7_003.data.settings.SettingId
import com.pocket_plan.j7_003.data.settings.SettingsManager
import kotlinx.android.synthetic.main.dialog_pick_time.view.*
import kotlinx.android.synthetic.main.fragment_sleep.*
import kotlinx.android.synthetic.main.fragment_sleep.view.*
import kotlinx.android.synthetic.main.row_sleep.view.*
import kotlinx.android.synthetic.main.title_dialog.view.*
import org.threeten.bp.DayOfWeek

/**
 * A simple [Fragment] subclass.
 */

class SleepFr : Fragment() {

    lateinit var myActivity: MainActivity
    lateinit var sleepReminderInstance: SleepReminder


    companion object {
        lateinit var myAdapter: SleepAdapter
    }

    private lateinit var regularCheckBoxList: ArrayList<CheckBox>
    private lateinit var regularWakeTimeText: TextView
    private lateinit var regularDurationTimeText: TextView

    private var customIsInit: Boolean = false
    private var regularIsInit: Boolean = false

    private val dark = SettingsManager.getSetting(SettingId.THEME_DARK) as Boolean

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myActivity = activity as MainActivity
        sleepReminderInstance = SleepReminder(myActivity)
        customIsInit = false
        regularIsInit = false

        val myView = inflater.inflate(R.layout.fragment_sleep, null)

        val myRecycler = myView.recycler_view_sleep
        sleepReminderInstance = SleepReminder(myActivity)
        myAdapter = SleepAdapter(myActivity, this)
        myRecycler.adapter = myAdapter
        myRecycler.layoutManager = LinearLayoutManager(activity)
        myRecycler.setHasFixedSize(true)

        if (sleepReminderInstance.isAnySet()) {
            myView.switchEnableReminder.isChecked = true
        }

        if (sleepReminderInstance.daysAreCustom) {
            initializeCustomDaysDisplay(myView)
            myView.switchEnableCustomDays.isChecked = true
            updateCustomDisplay()
            myView.panelNotCustom.visibility = View.GONE
            myView.panelCustom.visibility = View.VISIBLE
        } else {
            initializeRegularDayDisplay(myView)
            updateRegularDisplay()
            myView.panelNotCustom.visibility = View.VISIBLE
            myView.panelCustom.visibility = View.GONE
            myView.switchEnableCustomDays.isChecked = false
            myAdapter.notifyDataSetChanged()
        }

        //switch to enable / disable entire reminder
        myView.switchEnableReminder.setOnClickListener {
            if (myView.switchEnableReminder.isChecked) {
                sleepReminderInstance.enableAll()
                if (sleepReminderInstance.daysAreCustom) {
                    myAdapter.notifyDataSetChanged()
                } else {
                    updateRegularCheckboxes()
                }
            } else {
                sleepReminderInstance.disableAll()
                if (sleepReminderInstance.daysAreCustom) {
                    myAdapter.notifyDataSetChanged()
                } else {
                    updateRegularCheckboxes()
                }
            }
        }

        //switch to enable use of custom days
        myView.switchEnableCustomDays.setOnClickListener {
            if (myView.switchEnableCustomDays.isChecked) {
                if (!customIsInit) initializeCustomDaysDisplay(myView); customIsInit = true
                sleepReminderInstance.setCustom()
                updateCustomDisplay()
                animationShowCustom(myView)
            } else {
                if (!regularIsInit) initializeRegularDayDisplay(myView); regularIsInit = true
                sleepReminderInstance.setRegular()
                updateRegularDisplay()
                animationShowRegular(myView)
            }
        }
        return myView
    }

    private fun updateRegularDisplay() {
        updateRegularTimes()
        updateRegularCheckboxes()
    }

    private fun updateCustomDisplay() {
        myAdapter.notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    private fun initializeCustomDaysDisplay(v: View) {
        v.switchEnableCustomDays.isChecked = true
    }

    @SuppressLint("InflateParams")
    private fun initializeRegularDayDisplay(v: View) {
        /**
         * initialize lists of regular checkboxes, text view for regular wake time, and text view
         * for regular sleep duration
         */

        regularWakeTimeText = v.tvWakeTime
        regularDurationTimeText = v.tvSleepDuration

        regularCheckBoxList = arrayListOf(
            v.cbMonday, v.cbTuesday, v.cbWednsday,
            v.cbThursday, v.cbFriday, v.cbSaturday, v.cbSunday
        )

        v.panelWakeTime.setOnClickListener {
            val timeSetListener =
                TimePickerDialog.OnTimeSetListener { _: TimePicker?, h: Int, m: Int ->
                    sleepReminderInstance.editAllWakeUp(
                        h, m
                    )
                    updateRegularDisplay()
                }
            val tpd = when (dark) {
                true -> TimePickerDialog(
                    myActivity,
                    timeSetListener,
                    sleepReminderInstance.reminder[DayOfWeek.MONDAY]?.getWakeHour()!!,
                    sleepReminderInstance.reminder[DayOfWeek.MONDAY]?.getWakeMinute()!!,
                    true
                )
                else -> TimePickerDialog(
                    myActivity,
                    R.style.DialogTheme,
                    timeSetListener,
                    sleepReminderInstance.reminder[DayOfWeek.MONDAY]?.getWakeHour()!!,
                    sleepReminderInstance.reminder[DayOfWeek.MONDAY]?.getWakeMinute()!!,
                    true
                )
            }
            tpd.show()
            tpd.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(
                    myActivity.colorForAttr(R.attr.colorOnBackGround)
                )
            tpd.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(
                    myActivity.colorForAttr(R.attr.colorOnBackGround)
                )
        }

        v.panelSleepDuration.setOnClickListener {
            /**
             * pick sleep duration for ALL days
             */
            val myDialogView = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_pick_time, null)

            myDialogView.npHour.minValue = 0
            myDialogView.npHour.maxValue = 23

            myDialogView.npMinute.minValue = 0
            myDialogView.npMinute.maxValue = 59

            myDialogView.tvHourMinuteDivider.text = "h"
            myDialogView.tvHourMinuteAttachment.text = "m"

            val myBuilder = AlertDialog.Builder(myActivity).setView(myDialogView)
            val customTitle = LayoutInflater.from(activity)
                .inflate(R.layout.title_dialog, null)

            customTitle.tvDialogTitle.text = resources.getText(R.string.sleepDuration)
            myBuilder.setCustomTitle(customTitle)

            val myAlertDialog = myBuilder.create()
            val dialogWindow = myAlertDialog.window
            dialogWindow?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialogWindow?.setGravity(Gravity.CENTER)
            myAlertDialog.show()

            myDialogView.npHour.value =
                sleepReminderInstance.reminder[DayOfWeek.MONDAY]?.duration?.toHours()?.toInt()!!
            myDialogView.npMinute.value =
                sleepReminderInstance.reminder[DayOfWeek.MONDAY]?.duration?.toMinutes()
                    ?.toInt()!! % 60

            myDialogView.btnApplyTime.setOnClickListener {
                sleepReminderInstance.editAllDuration(
                    myDialogView.npHour.value,
                    myDialogView.npMinute.value
                )
                myAlertDialog.dismiss()
                updateRegularDisplay()
            }
        }

        regularCheckBoxList.forEachIndexed { i, cb ->
            cb.setOnClickListener {
                val day = DayOfWeek.values()[i]
                if (cb.isChecked) {
                    sleepReminderInstance.reminder[day]?.enable(day)
                } else {
                    sleepReminderInstance.reminder[day]?.disable(day)
                }

                if (!sleepReminderInstance.isAnySet()) {
                    switchEnableReminder.isChecked = false
                } else if (!switchEnableReminder.isChecked) {
                    switchEnableReminder.isChecked = true
                }

            }
        }
    }

    private fun updateRegularTimes() {
        regularWakeTimeText.text =
            sleepReminderInstance.reminder[DayOfWeek.MONDAY]?.getWakeUpTimeString()
        regularDurationTimeText.text =
            sleepReminderInstance.reminder[DayOfWeek.MONDAY]?.getDurationTimeString()
    }

    private fun updateRegularCheckboxes() {
        regularCheckBoxList.forEachIndexed { i, cb ->
            cb.isChecked = sleepReminderInstance.reminder[DayOfWeek.values()[i]]?.isSet!!
        }
    }


    private fun animationShowCustom(v: View) {
        v.panelNotCustom.visibility = View.GONE
        val animationHide =
            AnimationUtils.loadAnimation(myActivity, R.anim.scale_down_reverse)
        animationHide.duration = 350
        animationHide.fillAfter = false
        v.panelNotCustom.startAnimation(animationHide)

        v.panelCustom.visibility = View.VISIBLE
        val animationShow =
            AnimationUtils.loadAnimation(myActivity, R.anim.scale_down)
        animationShow.duration = 700
        animationShow.fillAfter = false
        v.panelCustom.startAnimation(animationShow)
    }

    private fun animationShowRegular(v: View) {
        v.panelCustom.visibility = View.GONE
        val animationHide =
            AnimationUtils.loadAnimation(myActivity, R.anim.scale_down_reverse)
        animationHide.duration = 700
        animationHide.fillAfter = false
        v.panelCustom.startAnimation(animationHide)

        val params = v.recycler_view_sleep.layoutParams as ConstraintLayout.LayoutParams
        params.marginStart = 20000

        v.panelNotCustom.visibility = View.VISIBLE
        val animationShow =
            AnimationUtils.loadAnimation(myActivity, R.anim.scale_down)
        animationShow.duration = 350
        animationShow.fillAfter = false
        animationShow.startOffset = 280
        v.panelNotCustom.startAnimation(animationShow)
    }
}

class SleepAdapter(mainActivity: MainActivity, sleepFr: SleepFr) :
    RecyclerView.Adapter<SleepAdapter.SleepViewHolder>() {
    private val myFragment = sleepFr
    private val myActivity = mainActivity
    private val dark = SettingsManager.getSetting(SettingId.THEME_DARK) as Boolean
    private val round = SettingsManager.getSetting(SettingId.SHAPES_ROUND) as Boolean
    private val cr = myActivity.resources.getDimension(R.dimen.cornerRadius)


    private var dayStrings = arrayOf(
        myActivity.resources.getString(R.string.sleepMon),
        myActivity.resources.getString(R.string.sleepTue),
        myActivity.resources.getString(R.string.sleepWed),
        myActivity.resources.getString(R.string.sleepThu),
        myActivity.resources.getString(R.string.sleepFri),
        myActivity.resources.getString(R.string.sleepSat),
        myActivity.resources.getString(R.string.sleepSun)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_sleep, parent, false)
        return SleepViewHolder(itemView)
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: SleepViewHolder, position: Int) {
        val day = DayOfWeek.values()[position]
        holder.day = day

        if (round) {
            holder.itemView.crvSleep.radius = cr
        }

        //initialize the day string
        holder.itemView.tvDayString.text = dayStrings[position]

        //initialize the checked State of the reminder checkBox
        holder.itemView.cbRemindMe.isChecked =
            myFragment.sleepReminderInstance.reminder[day]?.isSet!!

        //initialize wake up time string
        holder.itemView.tvWakeTimeRow.text =
            myFragment.sleepReminderInstance.reminder[day]?.getWakeUpTimeString()

        //initialize duration string
        holder.itemView.tvDurationRow.text =
            myFragment.sleepReminderInstance.reminder[day]?.getDurationTimeString()


        //listener for checkbox enabling reminder
        holder.itemView.cbRemindMe.setOnClickListener {
            if (holder.itemView.cbRemindMe.isChecked) {
                myFragment.sleepReminderInstance.reminder[day]?.enable(day)
            } else {
                myFragment.sleepReminderInstance.reminder[day]?.disable(day)
            }

            if (!myFragment.sleepReminderInstance.isAnySet()) {
                myFragment.switchEnableReminder.isChecked = false
            } else if (!myFragment.switchEnableReminder.isChecked) {
                myFragment.switchEnableReminder.isChecked = true
            }
        }

        holder.itemView.clTapFieldDuration.setOnClickListener {
            val myDialogView = LayoutInflater.from(myActivity)
                .inflate(R.layout.dialog_pick_time, null)

            myDialogView.npHour.minValue = 0
            myDialogView.npHour.maxValue = 23

            myDialogView.npMinute.minValue = 0
            myDialogView.npMinute.maxValue = 59

            myDialogView.tvHourMinuteDivider.text = "h"
            myDialogView.tvHourMinuteAttachment.text = "m"

            val myBuilder2 = AlertDialog.Builder(myActivity).setView(myDialogView)
            val customTitle2 =
                LayoutInflater.from(myActivity).inflate(R.layout.title_dialog, null)
            customTitle2.tvDialogTitle.text = myActivity.getString(
                R.string.sleepDurationDay
            )
            myBuilder2.setCustomTitle(customTitle2)

            val myAlertDialog2 = myBuilder2.create()
            myAlertDialog2.show()

            myDialogView.npHour.value =
                myFragment.sleepReminderInstance.reminder[day]?.getDurationHour()!!
            myDialogView.npMinute.value =
                myFragment.sleepReminderInstance.reminder[day]?.getDurationMinute()!!

            myDialogView.btnApplyTime.setOnClickListener {
                myFragment.sleepReminderInstance.editDurationAtDay(
                    day,
                    myDialogView.npHour.value,
                    myDialogView.npMinute.value
                )
                myAlertDialog2.dismiss()
                SleepFr.myAdapter.notifyItemChanged(position)

            }

        }
        holder.itemView.clTapFieldWakeUp.setOnClickListener {
            val timeSetListener =
                TimePickerDialog.OnTimeSetListener { _: TimePicker?, h: Int, m: Int ->
                    myFragment.sleepReminderInstance.editWakeUpAtDay(day, h, m)
                    SleepFr.myAdapter.notifyItemChanged(position)
                }
            val tpd = when (dark) {
                true ->
                    TimePickerDialog(
                        myActivity,
                        timeSetListener,
                        myFragment.sleepReminderInstance.reminder[day]?.getWakeHour()!!,
                        myFragment.sleepReminderInstance.reminder[day]?.getWakeMinute()!!,
                        true
                    )
                else ->
                    TimePickerDialog(
                        myActivity,
                        R.style.DialogTheme,
                        timeSetListener,
                        myFragment.sleepReminderInstance.reminder[day]?.getWakeHour()!!,
                        myFragment.sleepReminderInstance.reminder[day]?.getWakeMinute()!!,
                        true
                    )
            }
            tpd.show()
            tpd.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(
                    myActivity.colorForAttr(R.attr.colorOnBackGround)
                )
            tpd.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(
                    myActivity.colorForAttr(R.attr.colorOnBackGround)
                )
        }
    }

    override fun getItemCount(): Int = 7

    class SleepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var day: DayOfWeek
    }

}
