application {
    mainClass.set("com.akkaserverless.hackathon.retailer.RetailerServiceKt")
}

dependencies {
    implementation(project(":item-service"))
}