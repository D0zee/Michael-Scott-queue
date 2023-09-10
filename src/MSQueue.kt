import java.util.concurrent.atomic.AtomicReference

class MSQueue<E> : Queue<E> {
    private val dummy = Node<E>(null)
    private var head: AtomicReference<Node<E>> = AtomicReference(dummy)
    private var tail: AtomicReference<Node<E>> = AtomicReference(dummy)

    override fun enqueue(element: E) {
        val node = Node(element)
        while(true){
            val tailSnap = tail.get()
            if (tailSnap == tail.get()){
                val next = tailSnap.next.get()
                if (next != null){
                    tail.compareAndSet(tailSnap, next)
                }
                else{
                    if (tailSnap.next.compareAndSet(null, node)) {
                        tail.compareAndSet(tailSnap, node)
                        return
                    }
                }
            }
        }
    }

    override fun dequeue(): E? {
        while(true){
            val headSnap = head.get()
            if (headSnap == head.get()){
                val tailSnap = tail.get()
                val next = headSnap.next.get()
                if (headSnap == tailSnap){
                    if (next == null) {
                        return null
                    }
                    tail.compareAndSet(tailSnap, next)
                }
                else{
                    if (head.compareAndSet(headSnap, next)){
                        val elem = next?.element
                        next?.element = null
                        return elem
                    }
                }
            }
        }
    }

    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    override fun validate() {
        check(tail.get().next.get() == null) {
            "At the end of the execution, `tail.next` must be `null`"
        }
        check(head.get().element == null) {
            "At the end of the execution, the dummy node shouldn't store an element"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = AtomicReference<Node<E>?>(null)
    }
}
