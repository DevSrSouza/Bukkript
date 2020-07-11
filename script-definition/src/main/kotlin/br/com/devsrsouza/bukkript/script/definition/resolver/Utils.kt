package br.com.devsrsouza.bukkript.script.definition.resolver

fun isPackageAvailable(fqnPackage: String) = Package.getPackage(fqnPackage) != null