
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MemoryManagementReplacementPolicies {
	
	
	class Input{
		int numberOfPages;
		int numberOfFrames;
		int numberOfPageRequests;
		int[] pageRequests;
		public Input(int pages, int frames, int prs) {
			this.numberOfPageRequests = prs;
			this.numberOfPages = pages;
			this.numberOfFrames = frames;
			this.pageRequests = new int[this.numberOfPageRequests];
		}
	}
    public String LRU(Input input) {
    	String ans = "LRU\n";
    	LinkedHashMap<Integer, Boolean> cache = new LinkedHashMap<Integer, Boolean>(input.numberOfPageRequests, 0.75f, true);
    	Map<Integer, Integer> assignments = new HashMap<Integer, Integer>();
    	int counter = 0;
    	int faults = 0;
    	for(int page: input.pageRequests) {
    		if(assignments.containsKey(page)) {
    			ans += "Page " + page + " already in frame " + assignments.get(page) + "\n";
    			cache.put(page, true);
    		}else if(assignments.size() < input.numberOfFrames) {
    			ans += "Page " + page + " loaded into frame " + counter + "\n";
    			assignments.put(page, counter);
    			cache.put(page, true);
    			counter++;
    			faults++;
    			
    		}else {
    			int replace = cache.keySet().iterator().next();
    			int assignedFrame = assignments.get(replace);
    			ans += "Page " + replace + " unloaded from frame " + assignedFrame + ", ";
    			assignments.remove(replace);
    			cache.remove(replace);
    			assignments.put(page, assignedFrame);
    			cache.put(page, true);
    			ans += "Page " + page + " loaded into Frame " + assignedFrame + "\n";
    			faults++;
    			
    		}
    	} 
    	ans += faults + " page faults\n";
    	return ans;
    }
	public String optimal(Input input) {
		String ans = "Optimal\n";
	
		Map<Integer, Queue<Integer>> uses = new HashMap<Integer, Queue<Integer>>();
		for(int i = 0; i < input.numberOfPageRequests; i++) {
			int page = input.pageRequests[i];
		    if(uses.containsKey(page)) {
		    	Queue<Integer> queue = uses.get(page);
		    	queue.add(i);
		    	uses.put(page, queue);
		    }else {
		    	Queue<Integer> queue = new LinkedList<Integer>();
		    	queue.add(i);
		    	uses.put(page, queue);
		    }
		}
		for(int page: uses.keySet()) {
	
				Queue<Integer> queue = uses.get(page);
				queue.add(Integer.MAX_VALUE);
				uses.put(page, queue);
			
		}
		class PageRequest{ 
			int page;
			int nextUse;
			int frame;
			public PageRequest(int page, int nu, int frame) {
				this.page = page;
				this.nextUse = nu;
				this.frame= frame;
			}
		}
		class PageRequestComparator implements Comparator<PageRequest>{

			
			public int compare(PageRequest o1, PageRequest o2) {
				int ans = -(o1.nextUse - o2.nextUse);
				if(ans == 0) {
					return o1.frame - o2.frame;
				}
				return ans;
			}
			
		}
		PriorityQueue<PageRequest> queue = new PriorityQueue<PageRequest>(input.numberOfPageRequests * 2, new PageRequestComparator());
		Map<Integer, Integer> assignments = new HashMap<Integer, Integer>();
		Map<Integer, PageRequest> assignedPageReqs = new HashMap<Integer, PageRequest>();
		
		int faults = 0;
		
		int counter = 0;
		for(int page: input.pageRequests) {
			if(assignments.containsKey(page)) {
				ans += "Page " + page + " already in frame " + assignments.get(page) + "\n";
				PageRequest pr =assignedPageReqs.get(page);
				queue.remove(pr);
				Queue<Integer> nextUses = uses.get(page);
				nextUses.remove();

				if(nextUses.size() > 0) {
					pr.nextUse = nextUses.peek();
					queue.add(pr);
					
				}
				uses.put(page, nextUses);
				
			}else if(assignments.size() < input.numberOfFrames) {
				ans += "Page " + page  + " loaded into frame " + counter + "\n";
				assignments.put(page, counter);
				Queue<Integer> nextUses = uses.get(page);
				nextUses.remove();
				PageRequest pr = new PageRequest(page, nextUses.peek(), counter);
				assignedPageReqs.put(page, pr);
				queue.add(pr);
				faults++;
				counter++;
				uses.put(page, nextUses);
				
			}else {
				PageRequest replace = queue.remove();
				ans += "Page " + replace.page + " unloaded from frame " + replace.frame + ", ";
				assignments.remove(replace.page);
				assignedPageReqs.remove(replace.page);
				assignments.put(page, replace.frame);
				ans += "Page " + page + " loaded into Frame " + replace.frame + "\n";
				Queue<Integer> nextUses = uses.get(page);
				nextUses.remove();
				if(nextUses.size() > 0) {
					PageRequest pr = new PageRequest(page, nextUses.peek(), replace.frame);
					assignedPageReqs.put(page, pr);
					queue.add(pr);
				}

				faults++;
				uses.put(page, nextUses);
			}
		}
		ans += faults + " page faults\n";
		
		return ans;
	}
	public String FIFO(Input input) {
		Queue<Integer> queue = new LinkedList<Integer>();
		Map<Integer, Integer> assignments = new HashMap<Integer, Integer>();
		int counter = 0;
		String ans = "FIFO\n";
		int faults = 0;
		for(int page: input.pageRequests) {
			if(assignments.containsKey(page)) {
				ans += "Page " + page + " already in frame "+ assignments.get(page) + "\n";
			}
			else if(assignments.size() < input.numberOfFrames) {
				assignments.put(page, counter++);
				ans += "Page " + page + " loaded into frame " + (counter - 1) + '\n';
				queue.add(page);
				faults++;
			}else {
				int removingPage = queue.remove();
				int assignedFrame = assignments.get(removingPage);
				ans += "Page " + removingPage + " unloaded from Frame " + assignedFrame + ", ";
				assignments.remove(removingPage);
				assignments.put(page, assignedFrame);
				ans += "Page " + page + " loaded into Frame " + assignedFrame + "\n";
				queue.add(page);
				faults++;
				
			}
		}
		ans += faults + " page faults\n";
		
		return ans;
	}
	public Input readFromFile() {
		String filename = "input.txt";
		
		Scanner kb;
		try {
			kb = new Scanner(new File(filename));
			String input = "";
			while(kb.hasNext()) {
				input += kb.nextLine() +  "\n";
			}
			String[]  inputs = input.split("\n");
			String[] split = inputs[0].split("\t");
			String temp = "";
			
	        int [] firstLine = new int[3];
	        int counter = 0;
	        inputs[0] += " ";
	        
 			for(char c: inputs[0].toCharArray()) {
				if("0123456789".indexOf(c) > -1) {

					temp += c;
				}else {
					if(temp.length() > 0) {
		
						firstLine[counter++] = Integer.parseInt(temp);
					}
					temp = "";
				}
				
				
			}
	        
	        Input inputModel = new Input(firstLine[0], firstLine[1], firstLine[2]);
	        counter = 1;
	        for(int i = 0; i < inputModel.numberOfPageRequests; i++) {
	        	inputModel.pageRequests[i] = Integer.parseInt(inputs[counter++]);
	        }
			return inputModel;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		
		
		
	}
	public void writeToFile(String ans)
	{
		String filename = "output.txt";
	    BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
		    writer.write(ans);
		     
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

	}
	public static void main(String[] args) {
		MemoryManagementReplacementPolicies mmrps = new MemoryManagementReplacementPolicies();
		MemoryManagementReplacementPolicies.Input  input = mmrps.readFromFile();
		String fifo = mmrps.FIFO(input);
		String optimal = mmrps.optimal(input);
		String lru = mmrps.LRU(input);
		
		String answer = fifo + "\n" + optimal + "\n" + lru + "\n";
	    mmrps.writeToFile(answer);
	}

}
