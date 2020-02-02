import java.util.*;
import java.io.*;

public class SchedulingAlgorithms {
	class Input {
		String type;
		int quantum = -1;
		int jobsNum;
		Job[] jobs;
		
	}
	class Job {
		int processNum;
		int arrivalTime;
		int cpuTime;
		int priority;
		int cpuBurst;
	
		public Job(int processNum, int arrivalTime, int cpuTime, int priority) {
			this.processNum = processNum;
			this.arrivalTime = arrivalTime;
			this.cpuTime = cpuTime;
			this.cpuBurst = this.cpuTime;
			this.priority = priority;
		}
		
	} 
	
	public String prioritySchedulingWithPreemp(Input input) {
		String ans = "";
		ans += input.type + "\n";
		PriorityQueue<Job> queue = new PriorityQueue<Job>(input.jobsNum * 2, new Comparator<Job>() {

			@Override
			public int compare(Job job1, Job job2) {
				int result = job1.priority  - job2.priority;
				//If two jobs with same priority, break tie with arrival time
				if(result == 0) {
					return job1.arrivalTime  - job2.arrivalTime;
				}
				return result;
				
			}
			
		});
		int time = 0;
		int waits[] = new int[input.jobsNum];
		Job[] jobs = input.jobs;
		int jobCounter = 0;
		int processed = 0;
		while(processed < jobs.length) {
			while(jobCounter < jobs.length && jobs[jobCounter].arrivalTime == time) {
				queue.add(jobs[jobCounter++]);
			}
			if(!queue.isEmpty()) {
				Job job = queue.remove();
				ans += time + "  " + job.processNum + "\n";
		        boolean interrupted = false;
				while(jobCounter < jobs.length && jobs[jobCounter].arrivalTime < time + job.cpuTime) {
					queue.add(jobs[jobCounter++]);

					if(!queue.isEmpty() && queue.peek().priority < job.priority) {
						job.cpuTime -= (queue.peek().arrivalTime - time);
						time = queue.peek().arrivalTime;
						queue.add(job);
						interrupted = true;
						break;
					}
					
					
				}
				if(!interrupted) {
					time += job.cpuTime;
					waits[job.processNum-1] = time - (job.arrivalTime + job.cpuBurst);
					
					
					processed += 1;
				}
			}else {
				time += 1;
			}
			
		}
		ans += this.averageWaitTime(waits); 		
				
		return ans;
	}
	public String prioritySchedulingNOPreemp(Input input) {
		String ans = "";
		ans += input.type + "\n";
		PriorityQueue<Job> queue = new PriorityQueue<Job>(input.jobsNum * 2, new Comparator<Job>() {

			@Override
			public int compare(Job job1, Job job2) {
				int result = job1.priority - job2.priority;
				//if two jobs with same priority,break tie with the arrival Time
				
				if(result == 0) {
					return  job1.arrivalTime - job2.arrivalTime;
				}
				return result;
			
			}
		});
		int time = 0;
		int waits [] = new int[input.jobsNum];
		Job[] jobs = input.jobs;
		int jobCounter = 0;
		int processed = 0;
		while(processed < jobs.length) {
			while(jobCounter < jobs.length && jobs[jobCounter].arrivalTime <= time) {
				queue.add(jobs[jobCounter++]);
			}
			if(!queue.isEmpty()) {
				Job job = queue.remove();
				ans += time + "  " + job.processNum + "\n";
				time += job.cpuBurst;
				waits[job.processNum-1] = time - (job.arrivalTime + job.cpuBurst);
				
				processed += 1;
			}else {
				time += 1;
			}
		}
		ans += this.averageWaitTime(waits);
		return ans;
	}
	
	public String shortestJobFirst(Input input) {
		
		String ans = "";
		ans += input.type + "\n";
		class JobComparator  implements Comparator<Job>{

			@Override
			public int compare(Job job1, Job job2) {
				int result= job1.cpuBurst - job2.cpuBurst;
				if(result == 0) {
					return job1.arrivalTime - job2.arrivalTime;
				}
				return result;
				
			}
			
					
			
		}
		PriorityQueue<Job> queue = new PriorityQueue<Job>(input.jobsNum * 2, new JobComparator());
		int[] waits = new int[input.jobsNum];
		Job[] jobs = input.jobs;
		int time = 0;
		int jobCounter = 0;
		int processed = 0;
		while(processed < jobs.length) {
			while(jobCounter < jobs.length && jobs[jobCounter].arrivalTime <= time) {
				queue.add(jobs[jobCounter++]);
			}
			if(!queue.isEmpty()) {
				Job job = queue.remove();
				ans += time + "  " + job.processNum + "\n";
				time += job.cpuBurst;
				waits[job.processNum-1] = time - (job.arrivalTime + job.cpuBurst);
				
				processed += 1;
			}else {
				time += 1;
			}
		}
		ans += this.averageWaitTime(waits);
		
		return ans;
		
		
	}
	public String averageWaitTime(int[] waits) {
		int sum = 0;
		for(int i = 0; i < waits.length; i++) {
			sum += waits[i];
		}
		return "AVG Waiting Time: "+ sum/(waits.length + 0.0);
	}
	
	
	public String roundRobin(Input input) {
		int quantum = input.quantum;
		Queue<Job> queue = new LinkedList<Job>();

		Job[] jobs = input.jobs;
		int[] waits = new int[input.jobsNum];
		int time = 0;
		int jobCounter = 1;
		
		queue.add(jobs[0]);
		String ans = "";
		ans += input.type + " " + input.quantum + "\n";
		while(!queue.isEmpty()) {
			Job job = queue.remove();
			
			
		   
		   
	
			ans += time + "  " + job.processNum + "\n";
			
			if((job.cpuTime + time) <= (quantum + time)) {
				time += job.cpuTime;
				 int waitTime =  time - (job.arrivalTime + job.cpuBurst) ;

				waitTime = time - (job.arrivalTime + job.cpuBurst);
				waits[job.processNum-1] = waitTime;

				if(jobCounter < jobs.length) {
					queue.add(jobs[jobCounter++]);
					
				}
				
				
			}else {
				time += quantum;
				
				if(jobCounter < jobs.length) {
				   while(jobCounter < jobs.length && jobs[jobCounter].arrivalTime <= time) {
					   queue.add(jobs[jobCounter++]);
				   }
				   
				}
				job.cpuTime -= quantum;
				queue.add(job);
			}
		}
		
		ans  += this.averageWaitTime(waits);
		return ans;
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
			e.printStackTrace();
		}
	

	}

	public static void main(String[] args) {
		
		SchedulingAlgorithms schedulingAlgorithms =new  SchedulingAlgorithms();
		SchedulingAlgorithms.Input input = schedulingAlgorithms.readFromFile();
		System.out.println(input.type);
		if(input.type.equals("RR")) {
			String ans = schedulingAlgorithms.roundRobin(input);
			schedulingAlgorithms.writeToFile(ans);
			
		}else if(input.type.equals("SJF")) {
			String ans = schedulingAlgorithms.shortestJobFirst(input);
			
			schedulingAlgorithms.writeToFile(ans);
		}else if(input.type.equals("PR_noPREMP")) {
			String ans = schedulingAlgorithms.prioritySchedulingNOPreemp(input);
			schedulingAlgorithms.writeToFile(ans);
		}else if(input.type.equals("PR_withPREMP")){
			String ans = schedulingAlgorithms.prioritySchedulingWithPreemp(input);
			schedulingAlgorithms.writeToFile(ans);
			
		}
	
		
		
		
	}
	public Input readFromFile() {
		
		
		Scanner kb;
		try {
			kb = new Scanner(new File("input.txt"));
			String input = "";
			while(kb.hasNext()) {
				input += kb.nextLine() +  "\n";
			}
			Input inputModel  = new Input();
			String[]  inputs = input.split("\n");
			String[] types = inputs[0].split(" ");
			inputModel.type = types[0];
			if(types.length > 1) {
				inputModel.quantum = Integer.parseInt(types[1].trim());
			}
			inputModel.jobsNum = Integer.parseInt(inputs[1].trim());
			Job[] jobs = new Job[inputModel.jobsNum];
			int counter = 0;
			for(int i = 2; i < inputs.length; i++) {
				String[] info = inputs[i].split(" ");
				int[]  data = new int[info.length];
 				for(int j = 0; j < info.length; j++) {
					data[j] = Integer.parseInt(info[j].trim());
				}
				jobs[counter++] = new Job(data[0],data[1], data[2], data[3]);
			}
			inputModel.jobs = jobs;
			//Sort the jobs by arrival time if they are not ordered
			Arrays.sort(inputModel.jobs, new Comparator<Job>() {

				@Override
				public int compare(Job o1, Job o2) {
					return o1.arrivalTime - o2.arrivalTime;
				}
				
			});
			return inputModel;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}

	
	

}
