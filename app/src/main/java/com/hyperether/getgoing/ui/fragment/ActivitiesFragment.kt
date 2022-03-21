package com.hyperether.getgoing.ui.fragment

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.hyperether.getgoing.R
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.model.CBDataFrame
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.ui.activity.LocationActivity
import com.hyperether.getgoing.ui.activity.MainActivity
import com.hyperether.getgoing.ui.activity.ShowDataActivity
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.viewmodel.RouteViewModel
import java.text.DecimalFormat


class ActivitiesFragment : DialogFragment() {
    private lateinit var whiteView: View
    private lateinit var goal: TextView
    private lateinit var walkingLabel: TextView
    private lateinit var minutesWalking: TextView
    private lateinit var minutesRunning: TextView
    private lateinit var minutesCycling: TextView
    private lateinit var kcal: TextView
    private lateinit var low: TextView
    private lateinit var medium: TextView
    private lateinit var high: TextView
    private lateinit var saveChanges: Button
    private lateinit var backBtn: ImageButton
    private lateinit var seekBar: SeekBar

    private lateinit var tv_fa_pb_mileage1:TextView
    private lateinit var iv_fa_rightarrow1:ImageView
    private lateinit var tv_fa_pb_mileage2:TextView
    private lateinit var iv_fa_rightarrow2:ImageView
    private lateinit var tv_fa_pb_mileage3:TextView
    private lateinit var iv_fa_rightarrow3:ImageView
    private lateinit var prbWalk:ProgressBar
    private lateinit var prbRun:ProgressBar
    private lateinit var prbRide:ProgressBar
    lateinit var routeViewModel: RouteViewModel
    private lateinit var mileageWalk:TextView
    private lateinit var mileageRun:TextView
    private lateinit var mileageRide:TextView
    private lateinit var routeX:List<Route>
    private var settings: SharedPreferences? = null
    private lateinit var model: CBDataFrame

    companion object {
        fun newInstance() = ActivitiesFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        settings = activity?.getSharedPreferences(Constants.PREF_FILE, 0)
        model = CBDataFrame.getInstance()!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View = inflater.inflate(R.layout.fragment_activities,container,false)
        setButtons(view)
        routeViewModel = ViewModelProviders.of(this).get(RouteViewModel::class.java)
        routeViewModel.getAllRoutes().observe(
            this
        ) { route ->
            fillProgBars(route)
            initFragmentTranactions(view,route)
        }
        return view
    }

    private fun fillProgBars(route: List<Route>?) {
        val sharedPref:SharedPref = SharedPref.newInstance()
        var goal:Int = sharedPref.getGoal()
        var sumWalk:Double = 0.0
        var sumRun:Double = 0.0
        var sumRide:Double = 0.0
        var walkPercentage:Int = 0
        var runPercentage:Int = 0
        var ridePercentage:Int = 0
        if (route != null) {
            for (item in route){
                if (item.activity_id == 1){
                    sumWalk += item.length
                }
                if (item.activity_id == 2){
                    sumRun += item.length
                }
                if (item.activity_id == 3){
                    sumRide += item.length
                }
            }
            Log.d(ActivitiesFragment::class.simpleName, "fillProgBars: $goal $sumWalk $sumRide $sumRun")
            if (sumWalk != 0.0){
                walkPercentage = ((sumWalk*100)/goal).toInt()
            }
            if (sumRun != 0.0){
                runPercentage = ((sumRun*100)/goal).toInt()
            }
            if (sumRide != 0.0) {
                ridePercentage = ((sumRide*100)/goal).toInt()
            }
            prbWalk.progress = walkPercentage
            prbRun.progress = runPercentage
            prbRide.progress = ridePercentage
            val df:DecimalFormat = DecimalFormat("#.##")
            mileageWalk.setText(df.format(sumWalk/1000)+"km")
            mileageRun.setText(df.format(sumRun/1000)+"km")
            mileageRide.setText(df.format(sumRide/1000)+"km")


        }
    }

    private fun setButtons(view: View) {
        prbWalk = view.findViewById(R.id.progressBar)
        prbRun = view.findViewById(R.id.progressBar2)
        prbRide = view.findViewById(R.id.progressBar3)
        mileageWalk = view.findViewById(R.id.tv_fa_pb_mileage1)
        mileageRun = view.findViewById(R.id.tv_fa_pb_mileage2)
        mileageRide = view.findViewById(R.id.tv_fa_pb_mileage3)

    }

    override fun onStart() {
        super.onStart()

        minutesWalking = requireView().findViewById(R.id.tv_fa_minutes)
        minutesRunning = requireView().findViewById(R.id.tv_fa_min2)
        minutesCycling = requireView().findViewById(R.id.tv_fa_min3)
        seekBar = requireView().findViewById(R.id.seekBar)
        goal = requireView().findViewById(R.id.tv_fa_goal)
        kcal = requireView().findViewById(R.id.tv_fa_kcal)

        seekBar.incrementProgressBy(10)

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT

        dialog?.window?.setLayout(width, height)

        initScreenDimen()
        initLabels()
        initProgressStringColor()
        initListeners()
    }

    private fun initFragmentTranactions(view: View?, route:List<Route>) {
        val sharedPref:SharedPref = SharedPref.newInstance()
        Log.d(ActivitiesFragment::class.simpleName, "Route: $route size ${route.size}")

        var routesWalk = 0
        var routesRun = 0
        var routesCycle = 0

        for (x in route){
            if (x.activity_id == Constants.WALK_ID){
                routesWalk += 1
            }
            if (x.activity_id == Constants.RUN_ID){
                routesRun += 1
            }
            if (x.activity_id == Constants.RIDE_ID){
                routesCycle += 1
            }
        }

        if (view != null) {
            tv_fa_pb_mileage1 = view.findViewById(R.id.tv_fa_pb_mileage1)
            tv_fa_pb_mileage1.setOnClickListener(View.OnClickListener {
                sharedPref.setClickedTypeShowData(Constants.WALK_ID)
                if (routesWalk > 0){
                    startAct();
                }else{
                    dialogMethod("No Walk Routes to show!",view);
                }
            })
            iv_fa_rightarrow1 = view.findViewById(R.id.iv_fa_rightarrow1)
            iv_fa_rightarrow1.setOnClickListener(View.OnClickListener {
                sharedPref.setClickedTypeShowData(Constants.WALK_ID)
                if (routesWalk > 0){
                    startAct();
                }else{
                    dialogMethod("No Walk Routes to show!", view);

                }
            })
            tv_fa_pb_mileage2 = view.findViewById(R.id.tv_fa_pb_mileage2)
            tv_fa_pb_mileage2.setOnClickListener(View.OnClickListener {
                sharedPref.setClickedTypeShowData(Constants.RUN_ID)
                if (routesRun > 0){
                    startAct();
                }else{
                    dialogMethod("No Run Routes to show!", view);

                }
            })
            iv_fa_rightarrow2 = view.findViewById(R.id.iv_fa_rightarrow2)
            iv_fa_rightarrow2.setOnClickListener(View.OnClickListener {
                sharedPref.setClickedTypeShowData(Constants.RUN_ID)
                if (routesRun > 0){
                    startAct();
                }else{
                    dialogMethod("No Run Routes to show!", view);

                }

            })
            tv_fa_pb_mileage3 = view.findViewById(R.id.tv_fa_pb_mileage3)
            tv_fa_pb_mileage3.setOnClickListener(View.OnClickListener {
                sharedPref.setClickedTypeShowData(Constants.RIDE_ID)
                if (routesCycle > 0){
                    startAct();
                }else{
                    dialogMethod("No Cycle Routes to show!", view);

                }

            })
            iv_fa_rightarrow3 = view.findViewById(R.id.iv_fa_rightarrow3)
            iv_fa_rightarrow3.setOnClickListener(View.OnClickListener {
                sharedPref.setClickedTypeShowData(Constants.RIDE_ID)
                if (routesCycle > 0){
                    startAct();
                }else{
                    dialogMethod("No Cycle Routes to show!", view);

                }

            })
        }
    }

    private fun dialogMethod(s: String, view: View) {
        val snackbar = Snackbar.make(view, "$s",
            Snackbar.LENGTH_LONG)
        snackbar.setTextColor(Color.WHITE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.BLACK)
        val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.textSize = 20f
        snackbar.show()
    }

    private fun startAct() {
        activity?.let{
            val intent = Intent (it, ShowDataActivity::class.java)
            it.startActivity(intent)
        }
    }


    private fun initScreenDimen() {
        if (MainActivity.ratio > 1.8) {
            whiteView = requireView().findViewById(R.id.view)
            goal = requireView().findViewById(R.id.tv_fa_goal)
            walkingLabel = requireView().findViewById(R.id.tv_fa_pb_walking)

            whiteView.layoutParams.height = 900

            val params = goal.layoutParams as MarginLayoutParams
            val params1 = seekBar.layoutParams as MarginLayoutParams
            val params2 = walkingLabel.layoutParams as MarginLayoutParams

            params.topMargin = 90
            params1.topMargin = 70
            params2.topMargin = 80

            walkingLabel.layoutParams = params2
            goal.layoutParams = params
            seekBar.layoutParams = params1
        }
    }

    private fun getTimeEstimates(dist: Int): IntArray {
        val returnValues = IntArray(3)

        returnValues[0] = (dist / (Constants.AVG_SPEED_WALK * 60)).toInt()
        returnValues[1] = (dist / (Constants.AVG_SPEED_RUN * 60)).toInt()
        returnValues[2] = (dist / (Constants.AVG_SPEED_CYCLING * 60)).toInt()

        return returnValues
    }

    private fun initLabels() {
        seekBar.progress = settings!!.getInt("goal", 5000)

        val progress = seekBar.progress
        val timeEstimates = getTimeEstimates(progress)

        goal.text = progress.toString()

        minutesWalking.text = timeEstimates[0].toString() + " min"
        minutesRunning.text = timeEstimates[1].toString() + " min"
        minutesCycling.text = timeEstimates[2].toString() + " min"

        val sharedPref:SharedPref = SharedPref.newInstance()
        sharedPref.setTimeEstimateWalk(timeEstimates[0])
        sharedPref.setTimeEstimateRun(timeEstimates[1])
        sharedPref.setTimeEstimateCycle(timeEstimates[2])

        kcal.text =
            "About " + (progress * 0.00112 * settings!!.getInt("weight", 0)).toInt().toShort() +
                    "kcal"
    }

    private fun initListeners() {
        backBtn = requireView().findViewById(R.id.ib_fa_back)
        saveChanges = requireView().findViewById(R.id.b_fa_save)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var progressVar = progress
                progressVar /= 10
                progressVar *= 10

                goal.text = progressVar.toString()

                when (progressVar) {
                    in 0..3333 -> {
                        low.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.light_theme_accent
                            )
                        )
                        medium.setTextColor(ContextCompat.getColor(context!!, R.color.mat_gray))
                        high.setTextColor(ContextCompat.getColor(context!!, R.color.mat_gray))
                    }
                    in 3334..6666 -> {
                        low.setTextColor(ContextCompat.getColor(context!!, R.color.mat_gray))
                        medium.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.light_theme_accent
                            )
                        )
                        high.setTextColor(ContextCompat.getColor(context!!, R.color.mat_gray))
                    }
                    in 6667..10000 -> {
                        low.setTextColor(ContextCompat.getColor(context!!, R.color.mat_gray))
                        medium.setTextColor(ContextCompat.getColor(context!!, R.color.mat_gray))
                        high.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.light_theme_accent
                            )
                        )
                    }
                }

                val timeEstimates: IntArray = getTimeEstimates(progressVar)

                minutesWalking.text = timeEstimates[0].toString() + " min"
                minutesRunning.text = timeEstimates[1].toString() + " min"
                minutesCycling.text = timeEstimates[2].toString() + " min"
                kcal.text = "About " + (progressVar * 0.00112 *
                        settings!!.getInt("weight", 0)).toInt().toShort() + "kcal"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        low.setOnClickListener { seekBar.progress = Constants.CONST_LOW_DIST }
        medium.setOnClickListener { seekBar.progress = Constants.CONST_MEDIUM_DIST }
        high.setOnClickListener { seekBar.progress = Constants.CONST_HIGH_DIST }
        backBtn.setOnClickListener { this.dialog?.dismiss() }
        saveChanges.setOnClickListener {
            val sharedPref: SharedPref = SharedPref.newInstance()
            sharedPref.test("test String")
            val fragmentSentCode = sharedPref.getSentFromFragmentCode()
            Log.d(ActivitiesFragment::class.simpleName, "initListeners: $fragmentSentCode")
            if (seekBar.progress == 0){
                val builder: AlertDialog.Builder? = activity?.let {
                    AlertDialog.Builder(it)
                }
                builder?.setMessage("Goal can not be 0")
                    ?.setNegativeButton("Ok",
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                        })
                builder?.create()
                builder?.show() //ok
            }else{
                SharedPref.newInstance().setGoal(seekBar.progress)
                Toast.makeText(context, "Your goal is updated", Toast.LENGTH_SHORT).show()
                val i:Int = 501
                if (i == fragmentSentCode){
                    Log.d(ActivitiesFragment::class.simpleName, "initListeners: $i $fragmentSentCode")
                    //k switch back to location activity
                    val intent = Intent(context,LocationActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    }

    private fun initProgressStringColor() {
        val progress = seekBar.progress

        low = requireView().findViewById(R.id.tv_fa_low)
        medium = requireView().findViewById(R.id.tv_fa_medium)
        high = requireView().findViewById(R.id.tv_fa_high)

        when (progress) {
            in 0..3333 -> {
                low.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_theme_accent
                    )
                )
                medium.setTextColor(ContextCompat.getColor(requireContext(), R.color.mat_gray))
                high.setTextColor(ContextCompat.getColor(requireContext(), R.color.mat_gray))
            }
            in 3334..6666 -> {
                low.setTextColor(ContextCompat.getColor(requireContext(), R.color.mat_gray))
                medium.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_theme_accent
                    )
                )
                high.setTextColor(ContextCompat.getColor(requireContext(), R.color.mat_gray))
            }
            in 6667..10000 -> {
                low.setTextColor(ContextCompat.getColor(requireContext(), R.color.mat_gray))
                medium.setTextColor(ContextCompat.getColor(requireContext(), R.color.mat_gray))
                high.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_theme_accent
                    )
                )
            }
        }
    }
}