package kmm.jacky.utilitylibrary

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}