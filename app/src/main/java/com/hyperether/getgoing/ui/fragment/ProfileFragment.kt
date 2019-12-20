package com.hyperether.getgoing.ui.fragment

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.hyperether.getgoing.R
import com.hyperether.getgoing.model.CBDataFrame
import com.hyperether.getgoing.ui.activity.MainActivity
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.utils.Constants.gender
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : DialogFragment() {

    private var model: CBDataFrame? = null
    private var rootViewGroup: ViewGroup? = null
    private var settings: SharedPreferences? = null

    private lateinit var genderImg: ImageView

    private lateinit var dataLabel: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView

    private lateinit var genderBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        settings = activity?.getSharedPreferences(Constants.PREF_FILE, 0)
        model = CBDataFrame.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        rootViewGroup = container

        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)
        genderImg = rootView.findViewById(R.id.iv_fp_gender)

        when (settings!!.getInt("gender", 0)) {
            0 -> genderImg.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_gendersign_male
                )
            )
            1 -> genderImg.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_light_gender_female_icon
                )
            )
            2 -> genderImg.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_light_gender_icon_trans
                )
            )
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()

        genderBtn = view!!.findViewById(R.id.ib_fp_gender)
        tvGender = view!!.findViewById(R.id.tv_fp_gender)
        tvAge = view!!.findViewById(R.id.tv_fp_age)
        tvHeight = view!!.findViewById(R.id.tv_fp_height)
        tvWeight = view!!.findViewById(R.id.tv_fp_weight)

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT

        dialog?.window?.setLayout(width, height)

        initScreenDimen()
        initLabels()
        initDialogs()
    }

    private fun initScreenDimen() {
        if (MainActivity.ratio > 1.8) {
            dataLabel = view!!.findViewById(R.id.tv_fp_mydata)

            val params = dataLabel.layoutParams as MarginLayoutParams
            val params1 = genderBtn.layoutParams as MarginLayoutParams

            params.topMargin = 60
            params1.topMargin = 100

            dataLabel.layoutParams = params
            genderBtn.layoutParams = params1
        }
    }

    private fun initDialogs() { //TODO: add data binding here
        genderBtn.setOnClickListener { view ->
            val id = "gender"
            createDialog(id, view).also { it?.show() }
        }
        ib_fp_age.setOnClickListener { view ->
            val id = "age"
            createDialog(id, view).also { it?.show() }
        }
        ib_fp_height.setOnClickListener { view ->
            val id = "height"
            createDialog(id, view).also { it?.show() }
        }
        ib_fp_weight.setOnClickListener { view ->
            val id = "weight"
            createDialog(id, view).also { it?.show() }
        }
        ib_fp_backbutton.setOnClickListener {this.dialog!!.dismiss()}
    }

    private fun createDialog(pID: String, pView: View): AlertDialog.Builder? {
        val genderBuilder: AlertDialog.Builder
        val ageBuilder: AlertDialog.Builder
        val weightBuilder: AlertDialog.Builder
        val heightBuilder: AlertDialog.Builder

        val inflater: LayoutInflater

        when (pID) {
            "gender" -> {
                genderBuilder = AlertDialog.Builder(pView.context)
                var newText = ""

                genderBuilder.setSingleChoiceItems(
                    R.array.genders,
                    settings!!.getInt("gender", 0)
                ) { _, which ->
                    {
                        val editor = settings!!.edit()

                        when (which) {
                            0 -> {
                                newText = "Male"
                                editor.putInt("gender", 0)
                                model!!.gender = gender.Male
                            }
                            1 -> {
                                newText = "Female"
                                editor.putInt("gender", 1)
                                model!!.gender = gender.Female
                            }
                            else -> {
                                newText = "Other"
                                editor.putInt("gender", 2)
                                model!!.gender = gender.Other
                            }
                        }
                        editor.apply()
                    }.invoke()
                }
                    .setPositiveButton("Confirm") { _, _ ->
                        {
                            tvGender.text = newText

                            when (newText) {
                                "Male" -> genderImg.setImageDrawable(
                                    ContextCompat.getDrawable
                                        (context!!, R.drawable.ic_gendersign_male)
                                )
                                "Female" -> genderImg.setImageDrawable(
                                    ContextCompat.getDrawable
                                        (context!!, R.drawable.ic_light_gender_female_icon)
                                )
                                "Other" -> genderImg.setImageDrawable(
                                    ContextCompat.getDrawable
                                        (context!!, R.drawable.ic_light_gender_icon_trans)
                                )
                            }
                        }()
                    }
                    .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.cancel() }
                    .setTitle("Please select your gender:")

                return genderBuilder
            }
            "age" -> {
                val ageList = arrayListOf<String>()
                for (i in 1..120)
                    ageList.add(i.toString())

                ageBuilder = AlertDialog.Builder(pView.context)
                inflater = LayoutInflater.from(pView.context)

                val toInflate = inflater.inflate(R.layout.alertdialog_age, rootViewGroup)
                ageBuilder.setView(toInflate)

                val ageSpinner: Spinner = toInflate.findViewById(R.id.dialog_spinner_age)
                val arAdapter = ArrayAdapter<String>(pView.context,
                    android.R.layout.simple_list_item_1, ageList)

                ageSpinner.adapter = arAdapter
                ageSpinner.setSelection(settings!!.getInt("age", 0) - 1)

                ageBuilder.setPositiveButton("Confirm") { _, _ -> {
                    tvAge.text = ageSpinner.selectedItem.toString() +
                            resources.getString(R.string.textview_age_end)

                    val editor = settings!!.edit()
                    editor.putInt("age",
                        Integer.valueOf((ageSpinner.selectedItem as String)))
                    editor.apply()

                    model!!.age = Integer.valueOf((ageSpinner.selectedItem as String))
                }()}
                    .setNegativeButton("Cancel") { dialogInterface, _ ->
                        dialogInterface.cancel()}
                    .setTitle("How old are you?")

                return ageBuilder
            }
            "height" -> {
                val heightList = arrayListOf<String>()
                for (i in 110..250) {
                    heightList.add(i.toString())
                }

                heightBuilder = AlertDialog.Builder(pView.context)
                inflater = LayoutInflater.from(pView.context)

                val toInflate = inflater.inflate(R.layout.alertdialog_height, rootViewGroup)
                heightBuilder.setView(toInflate)

                val heightSpinner: Spinner = toInflate.findViewById(R.id.dialog_spinner_height)
                val arAdapter = ArrayAdapter<String>(pView.context,
                    android.R.layout.simple_list_item_1, heightList)

                heightSpinner.adapter = arAdapter
                heightSpinner.setSelection(settings!!.getInt("height", 0) - 110)

                heightBuilder.setPositiveButton("Confirm") { _, _ -> {
                    tvHeight.text = heightSpinner.selectedItem.toString() + " cm"

                    val editor = settings!!.edit()
                    editor.putInt("height", Integer.valueOf(heightSpinner.selectedItem as String))
                    editor.apply()
                    model?.height = Integer.valueOf(heightSpinner.selectedItem as String)
                }()}
                    .setNegativeButton("Cancel") {dialogInterface, _ -> dialogInterface.cancel()}
                    .setTitle("Enter your height:")

                return heightBuilder
            }
            "weight" -> {
                val weightList = arrayListOf<String>()
                for (i in 40..150) {
                    weightList.add(i.toString())
                }

                weightBuilder = AlertDialog.Builder(pView.context)
                inflater = LayoutInflater.from(pView.context)

                val toInflate = inflater.inflate(R.layout.alertdialog_weight, rootViewGroup)
                weightBuilder.setView(toInflate)

                val weightSpinner: Spinner = toInflate.findViewById(R.id.dialog_spinner_weight)
                val arAdapter = ArrayAdapter<String>(pView.context,
                    android.R.layout.simple_list_item_1, weightList)

                weightSpinner.adapter = arAdapter
                weightSpinner.setSelection(settings!!.getInt("weight", 0) - 40)

                weightBuilder.setPositiveButton("Confirm") {_, _ -> {
                    tvWeight.text = weightSpinner.selectedItem.toString() + " kg"

                    val editor = settings!!.edit()
                    editor.putInt("weight", Integer.valueOf(weightSpinner.selectedItem as String))
                    editor.apply()
                    model?.weight = Integer.valueOf(weightSpinner.selectedItem as String)
                }()}
                    .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.cancel()}
                    .setTitle("Enter your weight:")

                return weightBuilder
            }
        }

        return null
    }

    private fun initLabels() {
        tvAge.text = settings!!.getInt("age", 0).toString() + " years"
        tvHeight.text = settings!!.getInt("height", 0).toString() + "cm"
        tvWeight.text = settings!!.getInt("weight", 0).toString() + "kg"

        when (settings!!.getInt("gender", 0)) {
            0 -> tvGender.setText(R.string.gender_male)
            1 -> tvGender.setText(R.string.gender_female)
            2 -> tvGender.setText(R.string.gender_other)
        }
    }

}