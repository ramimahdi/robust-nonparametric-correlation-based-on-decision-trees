package TreesBasedCorr;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

public class QSortAlg
{
    private static void QuickSortASCE(SortItem a[], int lo0, int hi0)
    {
        int lo = lo0;
        int hi = hi0;
        double mid;

        if ( hi0 > lo0)
        {
            mid = a[ ( lo0 + hi0 ) / 2 ].getValue();

            while( lo <= hi )
            {
                while( ( lo < hi0 )  && ( a[lo].getValue() < mid ))
                    ++lo;

                while( ( hi > lo0 ) && ( a[hi].getValue() > mid ))
                    --hi;

                if( lo <= hi )
                {
                    swap(a, lo, hi);
                    ++lo;
                    --hi;
                }
            }


            if( lo0 < hi )
                QuickSortASCE( a, lo0, hi );

            if( lo < hi0 )
                QuickSortASCE( a, lo, hi0 );

        }
    }

    private static void swap(SortItem a[], int i, int j)
    {
        SortItem T;
        T = a[i];
        a[i] = a[j];
        a[j] = T;

    }

    public static void sortASCE(float a[])
    {
        SortableDouble[] temp = SortableDouble.toSortableDouble(a);
        QuickSortASCE(temp, 0, temp.length - 1);
        SortableDouble.toDouble(temp,a );
    }

    public static int[] getIndexesSortASCE(float a[])
    {
        SortableDouble[] temp = SortableDouble.toSortableDouble(a);
        QuickSortASCE(temp, 0, temp.length - 1);
        return SortableDouble.toSortedIndexes(temp);
    }

    public static int[] getIndexesSortASCE(int a[])
    {
        SortableDouble[] temp = SortableDouble.toSortableDouble(a);
        QuickSortASCE(temp, 0, temp.length - 1);
        return SortableDouble.toSortedIndexes(temp);
    }

    public static int[] getIndexesSortASCE(double a[])
    {
        SortableDouble[] temp = SortableDouble.toSortableDouble(a);
        QuickSortASCE(temp, 0, temp.length - 1);
        return SortableDouble.toSortedIndexes(temp);
    }
}

class SortableDouble implements SortItem, Comparable<SortableDouble> {
    double v;
    int original_ind;
    public SortableDouble(int original_ind,double v){
        this.v=v;
        this.original_ind=original_ind;
    }

    public int compareTo(SortableDouble other) {
        return Double.compare(this.v, other.v);
    }

    public double getValue(){
        return v;
    }

    public static SortableDouble[] toSortableDouble(double[] vs) {
        SortableDouble[] sds = new SortableDouble[vs.length];
        for(int i=0;i<sds.length;i++){
            sds[i] = new SortableDouble(i,vs[i]);
        }
        return sds;
    }

    public static SortableDouble[] toSortableDouble(int[] vs) {
        SortableDouble[] sds = new SortableDouble[vs.length];
        for(int i=0;i<sds.length;i++){
            sds[i] = new SortableDouble(i,vs[i]);
        }
        return sds;
    }

    public static SortableDouble[] toSortableDouble(float[] vs) {
        SortableDouble[] sds = new SortableDouble[vs.length];
        for(int i=0;i<sds.length;i++){
            sds[i] = new SortableDouble(i,vs[i]);
        }
        return sds;
    }

    public static void toDouble(SortableDouble[] sds,float[] vs ){
        for(int i=0;i<sds.length;i++){
            vs[i]=(float)sds[i].v;
        }
    }

    public static int[] toSortedIndexes(SortableDouble[] sds ){
        int[] ind=new int[sds.length];
        for(int i=0;i<sds.length;i++){
            ind[i]=sds[i].original_ind;
        }
        return ind;
    }
}