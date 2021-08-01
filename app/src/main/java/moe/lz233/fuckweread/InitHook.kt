package moe.lz233.fuckweread

import android.app.Application
import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import moe.lz233.fuckweread.utils.LogUtil
import moe.lz233.fuckweread.utils.ModuleContext
import moe.lz233.fuckweread.utils.ktx.hookAfterMethod
import java.io.File

class InitHook : IXposedHookLoadPackage {
    lateinit var segmentFile: File
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == PACKAGE_NAME) {
            Application::class.java
                .hookAfterMethod("attach", Context::class.java) {
                    ModuleContext.context = it.args[0] as Context
                    ModuleContext.classLoader = ModuleContext.context.classLoader
                    initHook()
                }
        }
    }

    private fun initHook() {
        //var segmentIndex = 1
        /*"com.tencent.weread.cleaner.PathStorage".hookAfterMethod(
            "getSegmentIndexPath",
            String::class.java,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        ) {
            //segmentIndex = it.args[1] as Int
            File("/data/data/$PACKAGE_NAME/fuckWeRead/${it.args[0] as String}").mkdirs()
            segmentFile = File(
                "/data/data/$PACKAGE_NAME/fuckWeRead/${it.args[0] as String}",
                (it.args[1] as Int).toString()
            ).apply { createNewFile() }
        }*/
        "com.tencent.weread.reader.storage.BookStorage".hookAfterMethod(
            "getStyleIndexPath",
            String::class.java,
            Int::class.javaPrimitiveType
        ) {
            File("/data/data/$PACKAGE_NAME/fuckWeRead/${it.args[0] as String}").mkdirs()
            segmentFile = File(
                "/data/data/$PACKAGE_NAME/fuckWeRead/${it.args[0] as String}",
                (it.args[1] as Int).toString()
            ).apply { createNewFile() }
        }
        /*"com.tencent.weread.reader.segment.SegmentParser".hookAfterMethod(
            "read",
            StringBuilder::class.java,
            InputStream::class.java,
            ByteArray::class.java,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        ) {
            LogUtil.d(it.result as java.lang.StringBuilder)
            segmentFile.writeText((it.result as StringBuilder).toString())
        }
        "com.tencent.weread.reader.segment.SegmentParser".hookAfterMethod(
            "readLastPart",
            InputStream::class.java,
            ByteArray::class.java,
            Int::class.javaPrimitiveType
        ) {
            segmentFile.writeText(it.result as String)
        }*/
        "com.tencent.weread.reader.cursor.PageCursorWindow".hookAfterMethod(
            "getChars",
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        ) {
            LogUtil.d(String(it.result as CharArray))
            /*segmentFile.printWriter().use { out ->
                out.println(String(it.result as CharArray))
            }*/
            segmentFile.writeText("${segmentFile.readText()}${String(it.result as CharArray)}")
        }
    }
}