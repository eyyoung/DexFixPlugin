package me.young.android.gradle.dexfix

import com.android.build.api.transform.Format
import com.android.build.api.transform.Transform
import com.android.build.gradle.internal.dsl.DexOptions
import com.android.build.gradle.internal.pipeline.TransformTask
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy

public class DexFixPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("dexFix", DexFixExtention);
        def copyShrinkJar = project.getTasks().create("copyShrinkJar", Copy.class, new Action<Copy>() {
            @Override
            public void execute(Copy copy) {
                copy.from(project.zipTree(getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm())) {
                    include "*.jar"
                    include "*.rules"
                };
                copy.into(String.format("%s/dexfix", project.getBuildDir()));
            }
        });
        DexOptions dexOptions = project.android.dexOptions
        def mainDexPath = String.format("%s/dexfix/maindexlist.rule", project.getBuildDir())
        dexOptions.additionalParameters("--multi-dex",
                "--main-dex-list=${mainDexPath}".toString(),
                "--set-max-idx-number=64000")
        project.afterEvaluate {
            def applicationVariants = project.android.applicationVariants
            applicationVariants.each { variant ->
                boolean minifyEnabled = variant.buildType.minifyEnabled
                Task dexTask = null;
                File mergedJar = null;
                boolean hasAddCopyTask = false;
                project.tasks.matching {
                    ((it instanceof TransformTask) && it.name.endsWith(variant.name.capitalize()))
                }.each { Task task ->
                    if (!hasAddCopyTask) {
                        task.dependsOn(copyShrinkJar)
                        hasAddCopyTask = true
                    }
                    Transform transform = task.transform
                    def name = transform.name
                    if ("jarMerging".equalsIgnoreCase(name)) {
                        // JarMerging Task ,use to get the combined jar
                        def outputProvider = task.outputStream.asOutput()
                        mergedJar = outputProvider.getContentLocation("combined",
                                transform.getOutputTypes(),
                                transform.getScopes(), Format.JAR)
                    } else if ("dex".equalsIgnoreCase(name)) {
                        dexTask = task;
                    } else if (minifyEnabled && "proguard".equalsIgnoreCase(name)) {
                        def outputProvider = task.outputStream.asOutput()
                        mergedJar = outputProvider.getContentLocation("main",
                                transform.getOutputTypes(),
                                transform.getScopes(), Format.JAR)
                    }
                }
                if (dexTask != null && mergedJar != null) {
                    String mainDexRules = project.dexFix.mainDexRules
                    def ruleFile = new File(project.getProjectDir(), mainDexRules)
                    def proguardMainDexTask = new ProguardMainDex(project, mergedJar, ruleFile)
                    dexTask.doFirst({
                        proguardMainDexTask.proguardMainDex()
                    })
                }
            }
        }
    }

}