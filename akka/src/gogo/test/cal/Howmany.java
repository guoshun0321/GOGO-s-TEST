package gogo.test.cal;

import java.util.ArrayList;
import java.util.List;

public class Howmany
{

    public int howmany(String s)
    {

        Segments segs1 = new Segments();
        Segments segs2 = new Segments();

        int length = s.length();
        for (int i = 0; i < length; i++)
        {
            char c = s.charAt(i);
            if (c == '0')
            {
                if (segs1.num0 <= segs2.num0)
                {
                    segs1.addNum0(i);
                }
                else
                {
                    segs2.addNum0(i);
                }
            }
            else
            {
                if (segs1.num1 <= segs2.num1)
                {
                    segs1.addNum1(i);
                }
                else
                {
                    segs2.addNum1(i);
                }
            }
        }
        System.out.println(segs1);
        System.out.println(segs2);
        return segs1.segs.size() + segs2.segs.size();
    }

    class Segments
    {
        public List<Segment> segs;

        public Segment curSegment;

        public int num0;

        public int num1;

        public Segments()
        {
            segs = new ArrayList<Segment>();
            curSegment = new Segment();
            segs.add(curSegment);

            num0 = 0;
            num1 = 0;
        }

        public void addNum0(int seq)
        {
            if (curSegment.begin == -1)
            {
                curSegment.add(seq);
            }
            else
            {
                if ((seq - curSegment.end) == 1)
                {
                    curSegment.add(seq);
                }
                else
                {
                    curSegment = new Segment();
                    segs.add(curSegment);
                    curSegment.add(seq);
                }
            }
            num0++;
        }

        public void addNum1(int seq)
        {
            if (curSegment.begin == -1)
            {
                curSegment.add(seq);
            }
            else
            {
                if ((seq - curSegment.end) == 1)
                {
                    curSegment.add(seq);
                }
                else
                {
                    curSegment = new Segment();
                    segs.add(curSegment);
                    curSegment.add(seq);
                }
            }
            num1++;
        }
        
        @Override
        public String toString()
        {
            return segs.toString();
        }
    }

    class Segment
    {
        int begin;
        int end;

        public Segment()
        {
            begin = -1;
            end = -1;
        }

        public void add(int i)
        {
            if (begin == -1 || end == -1)
            {
                begin = i;
                end = i;
            }
            else
            {
                end = i;
            }
        }
        
        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            return sb.append("[").append(begin).append(",").append(end).append("]").toString();
        }
    }

    public static void main(String[] args)
    {
        Howmany many = new Howmany();
        System.out.println(many.howmany("010111"));
    }

}
