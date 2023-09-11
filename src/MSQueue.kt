import java.util.concurrent.atomic.AtomicReference

class MSQueue<E> : Queue<E> {
    private val dummy = Node<E>(null)
    private var head: AtomicReference<Node<E>> = AtomicReference(dummy)
    private var tail: AtomicReference<Node<E>> = AtomicReference(dummy)

    override fun enqueue(element: E) {
        val node = Node(element)
        var suc = false
        while (!suc) {
            val curTail = tail.get()
            suc = curTail.next.compareAndSet(null, node)
            tail.compareAndSet(curTail, curTail.next.get())
        }
    }

    override fun dequeue(): E? {
        while (true) {
            val curHead = head.get()
            val curTail = tail.get()
            val next = curHead.next.get()
            if (curHead == curTail) {
                if (next == null) {
                    return null
                }
                tail.compareAndSet(curHead, next)
            } else {
                if (head.compareAndSet(curHead, next)) {
                    val elem = next?.element
                    next?.element = null
                    return elem
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
