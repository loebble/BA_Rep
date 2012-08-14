package de.htwg_konstanz.in;

public class QueueString {

	public NodeString head, tail;
	
	public QueueString() {
		head = null;
		tail = null;
	}

	public boolean empty() {
		return (head == null);
	}

	public String get() {
		String v = head.value;
		head = head.next;
		return v;
	}

	public void put(String item) {
		NodeString t = tail;
		tail = new NodeString(item);
		if (empty()){
			head = tail;
		}
		else{
			t.next = tail;
		}		
	}
	
	public int size(){
		if(empty()) return 0;
		int size = 0;
		for(NodeString t = head; t != null; t = t.next){
			size++;
		}
		return size;
	}
	
	@Override
		public String toString() {
		if (empty()) {
			return "empty";
		}
			String content = "[";
			for(NodeString t = head; t != null; t = t.next){
				if(t.equals(head)){
					content += t.value; 
				}else content += ", " + t.value ;
			}
			return content + "]";
		}
}
