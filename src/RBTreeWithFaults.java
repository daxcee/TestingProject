import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * RBTree
 *
 * An implementation of a Red Black Tree with
 * non-negative, distinct integer keys and values
 *
 */

public class RBTreeWithFaults {
	public RBNode Root;

	/**
	 * O(1)
	 * @return the tree real root (not the sentinel)
	 */
	public RBNode getRoot(){
		return Root.Left;
	}

	/**
	 * O(1)
	 * Create a new empty tree
	 */
	public RBTreeWithFaults(){
		this.Root = createInfinityNode();
	}

	/**
	 * O(1)
	 * create a tree with a root
	 * @param root - RBNode object will be the root of the tree
	 */
	public RBTreeWithFaults(RBNode root){
		this.Root = createInfinityNode(root);
	}

	/**
	 * O(1)
	 * create infinity node with Null children
	 * @return infinity node with no children
	 */
	private RBNode createInfinityNode(){
		return createInfinityNode(createNullNode(null));
	}

	/**
	 * O(1)
	 * create infinity node with a left child (infinity node cant have right child)
	 * @param leftchild - the left child of the infinity node
	 * @return infinity node with a left child
	 */
	/*private*/ RBNode createInfinityNode(RBNode leftchild){
		//create infinity node with a key of the maximum integer available in java.
		RBNode node = new RBNode("Infinity",Integer.MAX_VALUE,leftchild,null,null);
		//set the parent of the new child to be the node
		node.Left.Parent = node;
		//set the infinity node to be black
		node.Black = true;
		return node;
	}

	/**
	 * O(1)
	 * create a null node
	 * @param parent - the parent of this null node
	 * @return the null node already connected to its parent
	 */
	/*private*/ RBNode createNullNode(RBNode parent){
		//create a null node. this node will have the minimum integer value available in java as a key
		RBNode newNode = new RBNode("N",Integer.MIN_VALUE,null,null,parent);
		//set the null node to be black (default is red)
		newNode.Black = true;
		return newNode;
	}

	/**
	 * O(1)
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {
		return isNullNode(this.Root.Left);
	}

	/**
	 * O(log(n))
	 * public String search(int k)
	 *
	 * returns the value of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k)
	{
		RBNode currNode = SearchNode(k,this.Root);
		//return null if the tree is empty
		if(currNode == null){
			return null;
		}

		//if not found return null
		if(isNullNode(currNode) || currNode.Key != k){
			return null;
		}

		//return the value of the node with the key = k
		return currNode.Value;
	}

	/**
	 * O(log(n))
	 * search for node
	 * @param k - the key to look for
	 * @param node - the node to start looking from
	 * @return if found return the node with the same key and if not found returns the nearest node for the location
	 * the node was supposed to be found. if the parent is the infinity node and not part of the tree
	 * returns null
	 */
	/*private*/ public RBNode SearchNode(int k,RBNode node){
		//if the node that was given is the sentinel recursively call the function with the tree root
		if (isInfinityNode(node)){
			return SearchNode(k,node.Left);
		}

		if(isNullNode(node)){
			//the node is not exist in the tree. we will return null if the tree is empty.
			if(isInfinityNode(node.Parent)){
				return null;
			}else{
				//we could not find the node so we will return the parent of the imaginary node if it was exist in the tree
				return node.Parent;
			}
		}

		if(node.Key > k){
			//if the current node key is bigger then the key we look for
			//recursively call searchNode with the left child of the current node
			return SearchNode(k,node.Left);
		}

		if(node.Key < k){
			//if the current node key is lower then the key we look for
			//recursively call searchNode with the right child of the current node
			return SearchNode(k,node.Right);
		}
		//if the current node key is not bigger and not lower it equals to the key we are looking for
		return node;
	}

	/**
	 * O(log(n))
	 * public int insert(int k, String v)
	 *
	 * inserts an item with key k and value v to the red black tree.
	 * the tree must remain valid (keep its invariants).
	 * returns the number of color switches, or 0 if no color switches were necessary.
	 * returns -1 if an item with key k already exists in the tree.
	 */
	public int insert(int k, String v) {
		//find the place we want to insert the new node
		RBNode y = SearchNode(k,this.Root);
		//create new node which y is its parent
		RBNode z = new RBNode(v,k,y);
		//counter counts the number of color changes
		int counter = 0;
		if(y == null){
			//empty tree
			this.Root.Left = z;
			z.Black = true;
			z.Parent = this.Root;
		}else{
			if(y.Key == z.Key){
				//an item with the key k is  already exist in the tree
				return -1;
			}

			if(z.Key < y.Key){
				//z will be a left child
				y.Left = z;
			}else{
				//z will be a right child
				y.Right = z;
			}

			//calling the method that will check if the tree is valid and if not fix it
			counter = fixUpTree(z);
		}

		return counter;
	}

	/**
	 * O(log(n))
	 * keep the red and black rules. this method will be called when the rules were been
	 * compromised and needs to be fix.
	 * @param z - the node to start the fix from
	 * @return the number of color changes that occured while fixing the tree
	 */
	/*private*/public int fixUpTree (RBNode z){
		//counter will count the number of color changes
		int counter = 0;
		//run until there is no problem with the red rule
		while(!z.Parent.Black){
			if(z.Parent == z.Parent.Parent.Left){
				//z parent is a left child
				RBNode y = z.Parent.Parent.Right;
				if(!y.Black){
					//case 1: z'w parent and uncle are red
					z.Parent.Black = true;
					y.Black = true;
					z.Parent.Parent.Black = false;
					z = z.Parent.Parent;

					//case 1 cost 3 color changes
					counter += 3;
				}else{
					if(z == z.Parent.Right){
						//case 2: z is a right child and its uncle is red. need to left rotate
						z = z.Parent;
						leftRotate(z);
					}
					//case 3: z is a left child and its uncle is red. need to right rotate
					z.Parent.Black = true;
					z.Parent.Parent.Black = false;
					rightRotate(z.Parent.Parent);
					counter += 2;
				}
			}else{
				//z parent is a right child
				RBNode y = z.Parent.Parent.Left;
				if(!y.Black){
					//case 1: z'w parent and uncle are red
					z.Parent.Black = true;
					y.Black = true;
					z.Parent.Parent.Black = false;
					z = z.Parent.Parent;

					//case 1 cost 3 color changes
					counter += 3;
				}else{
					if(z == z.Parent.Left){
						//case 2: z is a left child and its uncle is red. need to right rotate
						z = z.Parent;
						rightRotate(z);
					}
					//case 3: z is a right child and its uncle is red. need to left rotate
					z.Parent.Black = true;
					z.Parent.Parent.Black = false;
					leftRotate(z.Parent.Parent);

					counter += 2;
				}
			}
		}

		if(!this.Root.Left.Black){
			counter++;
			this.Root.Left.Black = true;
		}

		return counter;
	}

	/**
	 * O(1)
	 * put y as left child for x
	 * @param x - the parent node
	 * @param y - left child root
	 */
	public void leftChild(RBNode x,RBNode y){
		x.Left = y;
		y.Parent = x;
	}

	/**
	 * O(1)
	 * put y as right child of x
	 * @param x - the parent node
	 * @param y - the right child node
	 */
	public void rightChild(RBNode x,RBNode y){
		x.Right = y;
		y.Parent = x;
	}

	/**
	 * O(1)
	 * put y instead of x
	 * @param x - the original child
	 * @param y - the child after the change
	 */
	 public void transplate(RBNode x, RBNode y){
		if (x == x.Parent.Left){
			leftChild(x.Parent,y);
		}else{
			rightChild(x.Parent,y);
		}
	}

	/**
	 * O(1)
	 * rotate the tree left (according to the principles discussed in class and can be
	 * found in Book: Cormen - introduction to algorithms
	 * @param x - the node to rotate from
	 */
	public void leftRotate(RBNode x){
		RBNode y = x.Right;
		transplate(x,y);
		rightChild(x,y.Left);
		leftChild(y,x);
	}

	/**
	 * O(1)
	 * rotate the tree right (according to the principles discussed in class and can be
	 * found in Book: Cormen - introduction to algorithms
	 * @param y - the node to rotate from
	 */
	public void rightRotate(RBNode y){
		RBNode x = y.Left;
		transplate(y,x);
		leftChild(y,x.Right);
		rightChild(x,y);
	}

	/**
	 * O(log(n))
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of color switches, or 0 if no color switches were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k)
	{
		int counter = 0;
		RBNode z = SearchNode(k, this.Root);
		if(z.Key != k){
			//item with the key k could not be found
			return -1;
		}

		RBNode x;
		RBNode y = z;
		boolean isBlackOriginalY = y.Black;

		//z is the node we want to delete
		if(isNullNode(z.Left)){
			x = z.Right;
			transplate(z,z.Right);
		}else if(isNullNode(z.Right)){
			x = z.Left;
			transplate(z,z.Left);
		}else{
			y = minimumNode(z.Right);
			isBlackOriginalY = y.Black;
			x = y.Right;
			if(y.Parent == z){
				x.Parent = y;
			}else{
				transplate(y,y.Right);
				y.Right = z.Right;
				y.Right.Parent = y;
			}
			transplate(z,y);
			y.Left = z.Left;
			y.Left.Parent = y;
			y.Black = z.Black;
		}

		if(isBlackOriginalY){
			//we have a problem with the black rule that needs to be fixed
			counter = deleteFixup(x);
		}

		return counter;
	}

	/**
	 * O(log(n))
	 * fix the red and black rules after deleting a node.
	 * @param x - the node to start the fixing from
	 * @return number of color changes made while fixing the tree
	 */
	 public int deleteFixup(RBNode x){
		//number of color changes
		int counter = 0;
		//run until x is the tree root and as long as x is black
		while(x != this.Root.Left && x.Black){
			if(x == x.Parent.Left){
				//x is a left child
				RBNode w = x.Parent.Right;
				if(!w.Black){
					//case 1
					w.Black = true;
					x.Parent.Black = false;
					leftRotate(x.Parent);
					w = x.Parent.Right;
					counter += 2;
				}
				if(w.Left.Black && w.Right.Black){
					//case 2
					w.Black = false;
					x = x.Parent;
					counter += 1;
				}else
				{
					if(w.Right.Black){
						//case 3
						w.Left.Black = true;
						w.Black = false;
						rightRotate(w);
						w = x.Parent.Right;
						counter +=2;
					}
					//case 4
					w.Black = x.Parent.Black;
					x.Parent.Black = true;
					w.Right.Black = true;
					leftRotate(x.Parent);
					x = this.Root.Left;
					counter += 1;
				}
			}else{
				//x is a right child
				RBNode w = x.Parent.Left;
				if(!w.Black){
					//case 1
					w.Black = true;
					x.Parent.Black = false;
					rightRotate(x.Parent);
					w = x.Parent.Left;
					counter += 2;
				}
				if(w.Left.Black && w.Right.Black){
					//case 2
					w.Black = false;
					x = x.Parent;
					counter += 1;
				}else
				{
					if(w.Left.Black){
						//case 3
						w.Right.Black = true;
						w.Black = false;
						leftRotate(w);
						w = x.Parent.Left;
						counter +=2;
					}
					//case 4
					w.Black = x.Parent.Black;
					x.Parent.Black = true;
					w.Left.Black = true;
					rightRotate(x.Parent);
					x = this.Root.Left;
					counter +=1;
				}
			}
		}
		if(x.Black == false){
			x.Black = true;
			counter += 1;
		}

		return counter;
	}

	/**
	 *  O(log(n))
	 * public String min()
	 *
	 * Returns the value of the item with the smallest key in the tree,
	 * or null if the tree is empty
	 */
	public String min()
	{
		if(this.empty()){
			return null;
		}
		return minimumNode(this.Root.Left).Value;
	}

	/**
	 *  O(log(n))
	 * return the node with the minimal key in the tree
	 * @param node - the node to start looking from
	 * @return the minimal node of the tree
	 */
	/*private*/ public static RBNode minimumNode(RBNode node){
		if(isNullNode(node)){
			return null;
		}

		//next smaller node is null node so this is the minimal node
		if(isNullNode(node.Left)){
			return node;
		}

		return minimumNode(node.Left);
	}

	/**
	 * O(1)
	 * check if the given node is an infinity node
	 * @param node - the node to check
	 * @return true if it is an infinity node. O.W false
	 */
	public boolean isInfinityNode(RBNode node){
		return node.Key == Integer.MAX_VALUE;
	}

	/**
	 * O(1)
	 * check if the given node is a null node
	 * @param node - the node to check
	 * @return true if it is an null node. O.W false
	 */
	public static boolean isNullNode(RBNode node){
		return node.Key == Integer.MIN_VALUE;
	}

	/**
	 *  O(log(n))
	 * public String max()
	 *
	 * Returns the value of the item with the largest key in the tree,
	 * or null if the tree is empty
	 */
	public String max()
	{
		if(this.empty()){return null;
		}

		return maxValue(this.Root.Left);

	}

	/**
	 *  O(log(n))
	 * Returns the value of the item with the largest key in the tree,
	 * @param node the node to start looking from
	 * @return the value of the minimal node in the tree
	 */
	public static String maxValue(RBNode node){
		//next node is null node so return this node
		if(isNullNode(node.Right)){
			return node.Value;
		}

		return maxValue(node.Right);
	}

	/**
	 *  O(n)
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 */
	public int[] keysToArray()
	{
		String keysString = ElementsToString(this.Root,true);
		//keysString is a string of all the keys in the tree separated by ","
		if(keysString.equals("")){
			return new int[0];
		}
		return ArrayOfStringsToArrayOfInts(keysString.split(","));
	}

	/**
	 * O(strArr length)
	 * @param strArr - array of string values (that can be parsed into integers)
	 * @return array of the same values cast as int
	 */
	/*private*/public int[] ArrayOfStringsToArrayOfInts(String[] strArr){
		int[] arr = new int[strArr.length];
		for(int i=0; i< strArr.length;i++){
			arr[i] = Integer.parseInt(strArr[i]);
		}
		return arr;
	}


	/**
	 * O(n)
	 * @param node - the node we start from
	 * @param key - if true returns the string with keys elements else return the string with value elements
	 * @return a string with values\keys seperated with ,
	 */
	/*private*/ public String ElementsToString(RBNode node,boolean key){
		if(isNullNode(node)){
			return "";
		}

		//if called with the infinity node recursively call with the real root
		if(isInfinityNode(node)){
			return ElementsToString(node.Left,key);
		}

		String str;
		if(key){
			//str will be the key
			str = Integer.toString(node.Key);
		}else{
			//str will be the value
			str = node.Value;
		}

		//str is the element needs to be insert in the returning string
		if(!isNullNode(node.Left) && !isNullNode(node.Right)){
			//return all the elements, bigger and smaller then the current node
			return ElementsToString(node.Left,key) + "," + str + "," + ElementsToString(node.Right,key);
		}

		if(!isNullNode(node.Right)){
			//there is no lower elements return the current element + all the elements that bigger
			return str + "," + ElementsToString(node.Right,key);
		}

		if(!isNullNode(node.Left)){
			//there is no higher elements. return all the elements that are lower than the current element
			return ElementsToString(node.Left,key) + "," + str;
		}

		return str;
	}

	/**
	 * O(n)
	 * public String[] valuesToArray()
	 *
	 * Returns an array which contains all values in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 */
	public String[] valuesToArray()
	{
		String valuesString = ElementsToString(this.Root.Left,false);
		//valuesString is a string with all the values saperated with ","
		return valuesString.split(",");
	}

	/**
	 * O(n)
	 * print out this RBTree level by level.
	 */
	public void print() {
		Queue<RBNode> queue = new LinkedList<>();
		queue.add(this.Root);
		while (!queue.isEmpty()) {
			int size = queue.size();
			for (int i = 0; i < size; i++) {
				RBNode curNode = queue.poll();
				System.out.print(curNode.Key + " " + curNode.Value + " " + (curNode.Black? "black" : "red") + " | ");
				if (curNode.Left != null) {
					queue.add(curNode.Left);
				}
				if (curNode.Right != null) {
					queue.add(curNode.Right);
				}
			}
			System.out.print('\n');
		}
	}

	/**
	 * O(log(n))
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none
	 * postcondition: none
	 */
	public int size()
	{
		if(this.empty()){
			return 0;
		}

		return sizeCalc(this.Root.Left);
	}

	/**
	 * O(log(n))
	 * @param node the node to start from
	 * @return the amount of all nodes "under" that node
	 */
	public static int sizeCalc(RBNode node){
		//count from both sides
		if(!isNullNode(node.Left) && !isNullNode(node.Right)){
			return 1 + sizeCalc(node.Left) + sizeCalc(node.Right);
		}

		//count with higher side
		if(!isNullNode(node.Left)){
			return 1 + sizeCalc(node.Left);
		}

		//count with lower side
		if(!isNullNode(node.Right)){
			return 1 + sizeCalc(node.Right);
		}

		return 1;
	}

	/**
	 * public class RBNode
	 *
	 *	An implementation of a node for the RBTree.
	 */
	public class RBNode{
		private String Value;
		private int Key;


		public RBNode getLeft() {
			return Left;
		}

		public RBNode getRight() {
			return Right;
		}

		public RBNode getParent() {
			return Parent;
		}


		private RBNode Left,Right,Parent;
		private boolean Black;

		/**
		 * O(1)
		 * new node object
		 * @param value - value of the node
		 * @param key - the key of the node
		 * @param left - the left child node
		 * @param right - the right child node
		 * @param parent - the parent node
		 */
		public RBNode(String value,int key, RBNode left, RBNode right,RBNode parent){
			this.Value = value;
			this.Key = key;
			this.Left = left;
			this.Right = right;
			this.Parent = parent;
			this.Black = false;
		}

		/**
		 * O(1)
		 * create node with null nodes children
		 * @param value - the value for the node
		 * @param key - the key for the node
		 * @param parent - the parent node
		 */
		public RBNode(String value,int key, RBNode parent){
			this(value, key, null, null, parent);
			this.Left = createNullNode(this);
			this.Right = createNullNode(this);
		}


		public int getKey() {
			return Key;
		}
	}
	public static void main(String[] args) {

	}
}