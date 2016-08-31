package me.young.android.gradle.dexfix

import com.android.multidex.MainDexListBuilder
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import proguard.Configuration
import proguard.ConfigurationParser
import proguard.ProGuard

public class DexFilePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('proguardMainDex') << {
            def projectDir = project.getProjectDir()
            def classFile = new File(projectDir, "build\\intermediates\\packaged\\debug\\classes.jar")
            def path = classFile.absolutePath
            def tmp = String.format("%s/tmp/%s", project.getBuildDir(), "mainDexClasses-${new Random().nextInt()}.tmp.jar")
            def shrinkedAndroid = String.format("%s/dexfix/shrinkedAndroid.jar", project.getBuildDir())
            def rules = String.format("%s/dexfix/mainDexClasses.rules", project.getBuildDir())
            String[] strings = "-injars ${path} -dontwarn -forceprocessing -outjars ${tmp} -libraryjars ${shrinkedAndroid} -dontoptimize -dontobfuscate -dontpreverify -include ${rules}".split(" ")
            strings.each {
                string -> println(string)
            }
            proguard(strings)
            println("test2")
            MainDexListBuilder e = new MainDexListBuilder(true, tmp, path);
            Set toKeep = e.getMainDexList();
            new File(String.format("%s/dexfix/maindexlist.keep", project.getBuildDir())).withWriter { out ->
                toKeep.each {
                    out.println it
                }
            }
        }
        project.getTasks().create("copyShrinkJar", Copy.class, new Action<Copy>() {
            @Override
            public void execute(Copy copy) {
                copy.from(project.zipTree(getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm())) {
                    include "*.jar"
                    include "*.rules"
                };
                copy.into(String.format("%s/dexfix", project.getBuildDir()));
            }
        });
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