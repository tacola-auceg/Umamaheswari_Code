package org.apache.nutch.search.unl;
import java.util.*;
import java.io.*;
import java.lang.*;
import org.apache.nutch.unl.*;

import org.apache.nutch.analysis.unl.ta.FinalLLImpl;
import org.apache.nutch.analysis.unl.ta.ConceptNode;
import org.apache.nutch.analysis.unl.ta.ConceptToNode;
import org.apache.nutch.analysis.unl.ta.HeadNode;
import java.math.BigDecimal;

public class InvokeUNLSearch implements UNL
{
	public static int value=1;
	search_UNL searchObj =null;
	public static BufferedWriter bw1=null;
	boolean b=false;
	public static double searchTime ;
	public static Hashtable cachetable=new Hashtable();
	public static Stack LRU=new Stack();
        public static int qry_no=0;
	boolean online_process=false;
	public static int QUERYID=126;

	public InvokeUNLSearch()
	{
		
		searchObj = new search_UNL();

	}
	public static void main(String args[])
	{
                    //FileWriter fw = new FileWriter("/root/Desktop/Time.csv",true);
             
		
		String query = new String();
		ArrayList result=new ArrayList();
		InvokeUNLSearch inv = new InvokeUNLSearch();
		try
		{
			String str="";
			BufferedReader br=new BufferedReader(new FileReader("/root/query.txt"));
		
			while((str=br.readLine())!=null)
			
			{
				
				   long startTime=0;
			//System.out.println("QueryName"+str);
                long endTime=0;
                 startTime=System.nanoTime();
				query=str;
				result =inv.process(query.trim());
				//////////System.out.println(query);
                                qry_no++;
			
				int url_count=0;

                              try {   //try filewrite1 begins
                                        bw1 = new BufferedWriter(new FileWriter("/root/test-report/results.csv", true));
                                        java.util.Date dat = new java.util.Date();
                                        String date = dat.getDate() + "/3/" + (Integer.parseInt(String.valueOf(dat.getYear())) + 1900);
                                        ArrayList getList = new ArrayList();
                                        bw1.write(" ## ## ## ## ## ## ## ##\n");

                                        for (int k = 0; k < result.size(); k++)
                                        { //Till the result size for1 begins


					
                                            if (result.size() != k) {
                                                getList = (ArrayList) result.get(k);
					//System.out.println("Size of getList"+getList.size()); 
                                                if (getList.size() > 25)
                                                {
                                                    for (int i = 0; i < 20; i++)
                                                    {
                                                        Object[] result1 = (Object[]) getList.get(i);
                                                        String url = result1[0].toString();
                                                        if(url_count<20&&url!=null)
                                                        {
                                                        bw1.write(qry_no+"##"+query + "##" + date + "##" + result1[0].toString() + "##"+"d"+result1[1].toString()+".txt" +"##\n");

                                                        url_count++;
                                                        }
                                                        ////System.out.println("The list of URL" + url);
                                                    }

                                                } else
                                                {
                                                    for (int i = 0; i < getList.size(); i++)
                                                    {
						                                Object[] result1 = (Object[]) getList.get(i);
										
						                                String url = result1[0].toString();
                                                        if(url_count<20&&url!=null)
                                                        {
						                                bw1.write(qry_no+"##"+query + "##" + date + "##" + result1[0].toString() + "##"+"d"+result1[1].toString()+".txt" +"##\n");
//System.out.println(qry_no+"##"+query + "##" + date + "##" + result1[0].toString() + "##"+"d"+result1[1].toString()+".txt" +"##\n");
						                                url_count++;
                                                        }
                                                       ////System.out.println("The list of URL1" + url);
                                                    }
                                                }
                                            }
                                        } //Till the result size for1 ends
                                        bw1.close();
                                    } catch (Exception e) { //try filewrite1 ends

                                        ////////System.out.println("Writing Exception:" + e.getMessage());
                                    }
				
                                      endTime=System.nanoTime(); 
                       double resulttime = endTime - startTime;		
		resulttime= resulttime / (Math.pow(10, 9)); 
//                            fw.write(BigDecimal.valueOf(result)+", ");
		System.out.println("TimeTaken"+BigDecimal.valueOf(resulttime));
		//System.gc();
                           //  fw.close();
			}
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public  ArrayList process(String queryString)
	{  
			int doc_rank=0;
		   ArrayList result= new ArrayList();
 		   ArrayList getList=new ArrayList();
		   String res1="";	
		   String qout="",qout1="";
		   int count=1;
		   		   
		online_process=true;
		   if(InvokeUNLSearch.cachetable.containsKey(queryString.trim()))
	           {
			//	//////////System.out.println(" query contain in cache="+queryString);
				cacheResult res=(cacheResult)InvokeUNLSearch.cachetable.get(queryString.trim());
				int counter=res.getCounter();
				res.setCounter(counter);
				InvokeUNLSearch.cachetable.put(queryString.trim(),res);
				InvokeUNLSearch.LRU.remove(queryString.trim());
				InvokeUNLSearch.LRU.push(queryString.trim());
				 result=res.getResult();
			////////////System.out.println("Result"+result);
			return result;
		 
		   }
		   else
		  {
		   
			try{			
				
        			CoreeObject obj = new CoreeObject();			
	        		obj.setActualQuery(queryString);
	        		obj.setLanguage("ta");        		
				CoreeObject objx = new QueryProcessing().processQuery(obj);				
				FinalLLImpl[] ll = new QueryTranslation().getQuerygraph(objx,online_process);

				for (int i = 0; i < 1; i++) {
		                HeadNode h1 = new HeadNode();
		                h1 = ll[i].head;
		                ConceptNode c11 = new ConceptNode();
		                c11 = h1.colnext;
		                ConceptToNode c22 = new ConceptToNode();
		                while (c11 != null) {
		                    c22 = c11.rownext;
		                    //System.out.println("concept in Miltilist[" + i + "] :" + c11.gn_word+"\t"+c11.poscheck + "\t" + c11.uwconcept + "\t" + c11.queryTag);
		                    if (c22 != null) {
		                        while (c22 != null) {
		                       //     //System.out.println("rownext id: "+c22.uwtoconcept+"\t"+ c22.relnlabel);
		                            c22 = c22.rownext;
		                        }
		                    }
		                    ////////System.out.println("\n");
		                    c11 = c11.colnext;
		                }
		            }
	        		result = searchObj.crc_UNLSearch(queryString,ll);		  				
				      				  	
			}catch(Exception e){e.printStackTrace();}
		
		 	 try
		 	 {
				 //////////System.out.println("after trans searching query="+queryString);
				 //result = searchObj.crc_UNLSearch(queryString);
				////////System.out.println("after trans searching Result="+result.size());
			if(!InvokeUNLSearch.cachetable.containsKey(queryString.trim()))
			{
				cacheResult res=new cacheResult();
				res.setResult(result);
				int counter=res.getCounter();
				res.setCounter(counter);
				InvokeUNLSearch.cachetable.put(queryString.trim(),res);
				InvokeUNLSearch.LRU.push(queryString.trim());
			}
			if(InvokeUNLSearch.LRU.size()>10)
			{

				CacheManagement.removeLeastReqFromcache(InvokeUNLSearch.LRU);


			}
			b=true;			
		  }
		  catch(Exception e)
		  {
			e.printStackTrace();
                   } 

		
		/*if(getList!=null)
		{
			bw1.write("\n"+queryString+"# ");
			//////////System.out.println("Inside test");
			//////////System.out.println("Inside test");
			Object[] result2=(Object[])getList.get(0);
			String url1=result2[0].toString();
			//////////System.out.println("The list of URL1"+url1);
			if(getList.size()>20)
			{
				for(int i=0;i<20;i++)
				{
				Object[] result1=(Object[])getList.get(i);
				String url=result1[0].toString();
				bw1.write(result1[0].toString()+"# ");
				//////////System.out.println("The list of URL1"+url);
				}	
				//bw1.write("\n");
			}
			else
			{
				for(int i=0;i<getList.size();i++)
				{
				Object[] result1=(Object[])getList.get(i);
				String url=result1[0].toString();
				bw1.write(result1[0].toString()+"# ");
				//////////System.out.println("The list of URL1"+url);
				}
				//bw1.write("\n");
			}
		//	//////////System.out.println("Size"+getList.size());
			//bw1.write(", "+"\n");
		
			
		}
		bw1.close();	
		
*/			 System.out.println("**********>Size:"+result.size());
			 return result;
			
		}	
		  
	}
}


