package me.young.android.gradle.dexfix

import com.android.multidex.MainDexListBuilder
import org.gradle.api.Project
import proguard.Configuration
import proguard.ConfigurationParser
import proguard.ProGuard;

/**
 * Created by Young on 2016/8/31.
 */
public class ProguardMainDex {

    private File combinedJarFile;
    private Project mProject
    private File mRuleFile

    public ProguardMainDex(Project project, File combinedJar, File ruleFile) {
        this.mRuleFile = ruleFile
        this.mProject = project
        this.combinedJarFile = combinedJar;
    }

    def proguardMainDex() {
        def tmp = String.format("%s/tmp/%s", mProject.getBuildDir(), "mainDexClasses-${new Random().nextInt()}.tmp.jar")
        def shrinkedAndroid = String.format("%s/dexfix/shrinkedAndroid.jar", mProject.getBuildDir())
        def rules = mRuleFile.absolutePath
        String[] strings = "-injars ${combinedJarFile.absolutePath} -dontwarn -forceprocessing -outjars ${tmp} -libraryjars ${shrinkedAndroid} -dontoptimize -dontobfuscate -dontpreverify -include ${rules}".split(" ")
        proguard(strings)
        MainDexListBuilder e = new MainDexListBuilder(true, tmp, combinedJarFile.absolutePath);
        Set toKeep = e.getMainDexList();
        new File(String.format("%s/dexfix/maindexlist.keep", mProject.getBuildDir())).withWriter { out ->
            toKeep.each {
                out.println it
            }
        }
    }

    private static void proguard(String[] strings) {
        Configuration var1 = new Configuration();
        try {
            ConfigurationParser var2 = new ConfigurationParser(strings, null);
            try {
                var2.parse(var1);
            } finally {
                var2.close();
            }
            (new ProGuard(var1)).execute();
        } catch (Exception var7) {
            if (var1.verbose) {
                var7.printStackTrace();
            } else {
                System.err.println("Error: " + var7.getMessage());
            }
        }
    }

}
