package com.hyperether.getgoing.ui.activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.hyperether.getgoing.R
import com.hyperether.getgoing.databinding.ActivityMainBinding
import com.hyperether.getgoing.model.CBDataFrame
import com.hyperether.getgoing.repository.callback.ZeroNodeInsertCallback
import com.hyperether.getgoing.repository.room.GgRepository
import com.hyperether.getgoing.repository.room.MapNode
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.ui.adapter.HorizontalListAdapter
import com.hyperether.getgoing.ui.adapter.formatter.MyProgressFormatter
import com.hyperether.getgoing.ui.adapter.formatter.MyProgressFormatter2
import com.hyperether.getgoing.ui.adapter.formatter.MyProgressFormatter3
import com.hyperether.getgoing.ui.fragment.ProfileFragment
import com.hyperether.getgoing.ui.handler.MainActivityClickHandler
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.utils.Constants.RIDE_ID
import com.hyperether.getgoing.utils.Constants.RUN_ID
import com.hyperether.getgoing.utils.Constants.WALK_ID
import com.hyperether.getgoing.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    public val TYPE = "type"
    private val PERMISSION_CODE = 1;

    companion object {
        var ratio: Float = 0f
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var mAdapter: HorizontalListAdapter
    private lateinit var centralImg: ImageView

    private lateinit var currentSettings: SharedPreferences
    private lateinit var model: CBDataFrame

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var rvm: RouteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentSettings = getSharedPreferences(Constants.PREF_FILE, 0)
        zeroNodeInit()

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.clickHandler = MainActivityClickHandler(supportFragmentManager)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), PERMISSION_CODE
            )
        }

        model = CBDataFrame.getInstance()!!

        initScreenDimen()
        initRecyclerView()
        initListeners()
    }

    private fun zeroNodeInit() {
        if (currentSettings.getBoolean("zeroNode", false) == false) { /*route init*/
            val tmpRoute: MutableList<MapNode> = ArrayList()
            val tmpNode = MapNode(0, 0.0, 0.0, 0F, 0, 0)

            tmpRoute.add(tmpNode)
            val dbRoute = Route(0, 0, 0.0, 0.0, "null", 0.0, 1, 0)
            GgRepository.insertRouteInit(dbRoute, tmpRoute, object : ZeroNodeInsertCallback {
                override fun onAdded() {
                    rvm = ViewModelProviders.of(this@MainActivity).get(RouteViewModel::class.java)
                    rvm.getLatestRoute()?.observe(this@MainActivity, Observer { initProgressBars(it) })
                }
            })

            val edit = currentSettings.edit()
            edit.putBoolean("zeroNode", true)
            edit.apply()
        }
    }

    private fun initProgressBars(route: Route?) { //isn't tested yet
        val lastRouteLen = route?.length
        val lastRouteTime: Int = if (route!!.duration >= 60000)
            (route.duration.toDouble() / 1000 / 60).roundToInt()
        else
            0

        val cpbProgress: Int = if (route.goal != 0)
            (lastRouteLen!! * 100 / route.goal).roundToInt()
        else
            0

        cpb_am_kmgoal.setProgressFormatter(MyProgressFormatter(lastRouteLen!!.toDouble()))
        cpb_am_kmgoal.progress = cpbProgress

        cpb_am_kmgoal1.setProgressFormatter(MyProgressFormatter2(lastRouteTime))
        cpb_am_kmgoal1.progress = 100

        cpb_am_kmgoal2.setProgressFormatter(MyProgressFormatter3())

        tv_am_kcalval.text = route.energy.toString()

        when (route.activity_id) {
            1 -> {
                tv_am_progbar_act.text = getString(R.string.activity_walking)
                iv_am_activity.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_walking_icon))
            }
            2 -> {
                tv_am_progbar_act.text = getString(R.string.activity_running)
                iv_am_activity.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_running_icon))
            }
            3 -> {
                tv_am_progbar_act.text = getString(R.string.activity_cycling)
                iv_am_activity.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_light_bicycling_icon
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        initModel()
    }

    private fun initModel() {
        model.measurementSystemId = currentSettings.getInt("measurementSystemId", Constants.METRIC)
        model.height = currentSettings.getInt("height", 0)
        model.weight = currentSettings.getInt("weight", 0)
        model.age = currentSettings.getInt("age", 0)
    }

    private fun initRecyclerView() {
        mRecyclerView = recyclerViewId
        mRecyclerView.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.layoutManager = layoutManager

        val DRAWABLE_MAP = SparseIntArray()
        DRAWABLE_MAP.append(
            R.drawable.ic_light_bicycling_icon_inactive,
            R.drawable.ic_light_bicycling_icon_active
        )
        DRAWABLE_MAP.append(
            R.drawable.ic_light_running_icon_inactive,
            R.drawable.ic_light_running_icon_active
        )
        DRAWABLE_MAP.append(R.drawable.ic_light_walking_icon, R.drawable.ic_light_walking_icon_active)

        mAdapter = HorizontalListAdapter(DRAWABLE_MAP, this)
        mRecyclerView.adapter = mAdapter

        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(mRecyclerView)

        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(layoutManager.itemCount / 2, -1)
    }

    private fun initListeners() {
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var i = 0
            var centralImgPos = IntArray(2)
            var selectorViewPos = IntArray(2)

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val centralLayout: View? = findCenterView(
                    layoutManager,
                    OrientationHelper.createOrientationHelper(layoutManager, RecyclerView.HORIZONTAL)
                )
                centralImg = centralLayout?.findViewById(R.id.iv_ri_pic)!!
                val k1 = centralLayout.let { layoutManager.getPosition(it) }

                when {
                    centralImg.tag?.equals(R.drawable.ic_light_bicycling_icon_inactive)!! -> tv_ma_mainact.text =
                        "Cycling"
                    centralImg.tag == R.drawable.ic_light_running_icon_inactive -> tv_ma_mainact.text =
                        "Running"
                    centralImg.tag == R.drawable.ic_light_walking_icon -> tv_ma_mainact.text = "Walking"
                }

                centralImg?.getLocationOnScreen(centralImgPos)

                if (i++ == 0) {
                    imageView2.getLocationOnScreen(selectorViewPos)
                }

                val centralImgWidthParam = centralImg!!.layoutParams.width / 2

                if (centralImgPos[0] > selectorViewPos[0] - centralImgWidthParam && centralImgPos[0] < selectorViewPos[0] + centralImgWidthParam) {
                    when (centralImg.tag) {
                        R.drawable.ic_light_bicycling_icon_inactive -> {
                            centralImg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.ic_light_bicycling_icon_active
                                )
                            )
                            centralImg.tag = R.drawable.ic_light_bicycling_icon_active
                        }
                        R.drawable.ic_light_running_icon_inactive -> {
                            centralImg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.ic_light_running_icon_active
                                )
                            )
                            centralImg.tag = R.drawable.ic_light_running_icon_active
                        }
                        R.drawable.ic_light_walking_icon -> {
                            centralImg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.ic_light_walking_icon_active
                                )
                            )
                            centralImg.tag = R.drawable.ic_light_walking_icon_active
                        }
                    }
                }

                val leftImg: ImageView?
                val rightImg: ImageView?

                try {
                    leftImg = layoutManager.findViewByPosition(k1 - 1)?.findViewById(R.id.iv_ri_pic)

                    when (leftImg?.tag) {
                        R.drawable.ic_light_bicycling_icon_active -> {
                            leftImg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.ic_light_bicycling_icon_inactive
                                )
                            )
                            leftImg.tag = R.drawable.ic_light_bicycling_icon_inactive
                        }
                        R.drawable.ic_light_running_icon_active -> {
                            leftImg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.ic_light_running_icon_inactive
                                )
                            )
                            leftImg.tag = R.drawable.ic_light_running_icon_inactive
                        }
                        R.drawable.ic_light_walking_icon_active -> {
                            leftImg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.ic_light_walking_icon
                                )
                            )
                            leftImg.tag = R.drawable.ic_light_walking_icon
                        }
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

                try {
                    rightImg = layoutManager.findViewByPosition(k1 + 1)?.findViewById(R.id.iv_ri_pic)

                    when (rightImg?.tag) {
                        R.drawable.ic_light_bicycling_icon_active -> {
                            rightImg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.ic_light_bicycling_icon_inactive
                                )
                            )
                            rightImg.tag = R.drawable.ic_light_bicycling_icon_inactive
                        }
                        R.drawable.ic_light_running_icon_active -> {
                            rightImg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.ic_light_running_icon_inactive
                                )
                            )
                            rightImg.tag = R.drawable.ic_light_running_icon_inactive
                        }
                        R.drawable.ic_light_walking_icon_active -> {
                            rightImg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.ic_light_walking_icon
                                )
                            )
                            rightImg.tag = R.drawable.ic_light_walking_icon
                        }
                    }
                } catch (e: java.lang.NullPointerException) {
                    e.printStackTrace()
                }
            }
        })

        materialButton.setOnClickListener {
            when (centralImg.tag) {
                R.drawable.ic_light_walking_icon_active -> callMeteringActivity(WALK_ID)
                R.drawable.ic_light_running_icon_active -> callMeteringActivity(RUN_ID)
                R.drawable.ic_light_bicycling_icon_active -> callMeteringActivity(RIDE_ID)
            }
        }
    }

    private fun findCenterView(layoutManager: RecyclerView.LayoutManager, helper: OrientationHelper): View? {
        val childCount = layoutManager.childCount

        if (childCount == 0) {
            return null
        }

        var closestChild: View? = null

        val center: Int = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + helper.totalSpace / 2
        } else {
            helper.end / 2
        }

        var absClosest = Int.MAX_VALUE

        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val childCenter = (helper.getDecoratedStart(child)
                    + helper.getDecoratedMeasurement(child) / 2)
            val absDistance = abs(childCenter - center)
            /** if child center is closer than previous closest, set it as closest   */
            if (absDistance < absClosest) {
                absClosest = absDistance
                closestChild = child
            }
        }
        return closestChild
    }

    private fun initScreenDimen() {
        val metrics = applicationContext.resources.displayMetrics
        ratio = metrics.heightPixels.toFloat() / metrics.widthPixels.toFloat()

        val unicode = 0x1F605 /* emoji */
        tv_am_burn.append(" " + String(Character.toChars(unicode)))

        if (ratio >= 1.8) {
            val params = tv_am_burn.layoutParams as MarginLayoutParams
            val params1 = iv_am_bluerectangle.layoutParams as MarginLayoutParams
            val params2 = tv_am_lastexercise.layoutParams as MarginLayoutParams

            iv_am_bluerectangle.layoutParams.height =
                ((cpb_am_kmgoal.layoutParams.height + cpb_am_kmgoal.layoutParams.height * 0.3).roundToInt())
            iv_am_bluerectangle.layoutParams.height = 650

            params.bottomMargin = 150
            params1.topMargin = 30
            params2.topMargin = 80

            tv_am_burn.layoutParams = params
            iv_am_bluerectangle.layoutParams = params1
            tv_am_lastexercise.layoutParams = params2
        }
    }

    fun callMeteringActivity(id: Int) {
        if (getParametersStatus(model)) {
            this.model.profileId = id
            val intent = Intent(this@MainActivity, LocationActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "You must enter your data first!", Toast.LENGTH_LONG).show()
            val profileFragment = ProfileFragment()
            profileFragment.show(supportFragmentManager, "ProfileFragment")
        }
    }

    private fun getParametersStatus(cbDataFrameLocal: CBDataFrame): Boolean {
        return !((cbDataFrameLocal.age == 0)
                || (cbDataFrameLocal.weight == 0))
    }
}