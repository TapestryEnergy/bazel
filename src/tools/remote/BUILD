load("@rules_java//java:defs.bzl", "java_binary")
load("@rules_oci//oci:defs.bzl", "oci_image", "oci_tarball")
load("@rules_pkg//:pkg.bzl", "pkg_tar")


filegroup(
    name = "srcs",
    srcs = glob(["**"]) + [
        "//src/tools/remote/src/main/java/com/google/devtools/build/remote/worker:srcs",
        "//src/tools/remote/src/test/java/com/google/devtools/build/remote/worker:srcs",
        "//src/tools/remote/src/test/java/com/google/devtools/build/remote/worker/http:srcs",
    ],
    visibility = ["//src:__pkg__"],
)

java_binary(
    name = "worker",
    main_class = "com.google.devtools.build.remote.worker.RemoteWorker",
    visibility = ["//visibility:public"],
    runtime_deps = ["//src/tools/remote/src/main/java/com/google/devtools/build/remote/worker"],
)

pkg_tar(
    name = "worker_pkg",
    srcs = [
        ":worker_deploy.jar",
    ],
)

oci_image(
    name = "worker_image",
    base = "@distroless_java",
    tars = [
        ":worker_pkg.tar"
    ],
    entrypoint = [
      "java",
      "-jar",
      "worker_deploy.jar",
    ],
)

oci_tarball(
    name = "worker_tarball",
    image = ":worker_image",
    repotags = [],
)