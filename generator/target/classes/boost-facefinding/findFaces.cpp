#include "stdafx.h"
#include <iostream>
#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/properties.hpp>
#include <boost/graph/graph_traits.hpp>
#include <boost/property_map/property_map.hpp>
#include <boost/ref.hpp>
#include <vector>

#include <boost/graph/planar_face_traversal.hpp>
#include <boost/graph/boyer_myrvold_planar_test.hpp>

#include <fstream>
#include <sstream>

using namespace boost;
using namespace std;
// Some planar face traversal visitors that will 
// print the vertices and edges on the faces

ofstream output_file;
ifstream input_file;

struct output_visitor : public planar_face_traversal_visitor
{
	void begin_face() {};
	void end_face() {
		if (output_file.is_open()){
			output_file << "\n";
		}
		//std::cout << std::endl;

	}
};



struct vertex_output_visitor : public output_visitor
{
	template <typename Vertex>
	void next_vertex(Vertex v)
	{
		//std::cout << v << " ";
		if (output_file.is_open()){
			output_file << v << " ";
		}
	}
};



struct edge_output_visitor : public output_visitor
{
	template <typename Edge>
	void next_edge(Edge e)
	{
		//std::cout << e << " ";
	}
};

int main(int argc, char** argv)
{

	bool fileExists = true;
	int fileNumber = 0; //The number of input file
	
	string fileName = "Edges" + std::to_string(fileNumber);

	while (fileExists){
		fileName = "Edges" + std::to_string(fileNumber);

		ifstream input_check(fileName);
		if (input_check)
		{
			fileNumber++;
		}
		else
		{
			if (fileNumber == 0){
				cout << "INPUTFILES DOES NOT EXIST!";
				return 0;
			}
			fileExists = false;
		}
	}

	cout << "NUMBER OF INPUT FILES: " << fileNumber;

	for (int i = 0; i < fileNumber; i++)
	{
		fileName = "Edges" + std::to_string(i);

		ifstream input_check(fileName);
		if (input_check)
		{
			input_file.open(fileName);
		}
		else
		{
			cout << "INPUTFILE DOES NOT EXIST!";
			return 0;
		}

		typedef adjacency_list
			< vecS,
			vecS,
			undirectedS,
			property<vertex_index_t, int>,
			property<edge_index_t, int>
			>
			graph;

		graph g(3);


		//Read the input
		string line;
		while (getline(input_file, line)){
			int node1;
			int node2;
			istringstream iss(line);
			if (!(iss >> node1 >> node2)){ break; }
			add_edge(node1, node2, g);
		}

		input_file.close();

		// Initialize the interior edge index
		property_map<graph, edge_index_t>::type e_index = get(edge_index, g);
		graph_traits<graph>::edges_size_type edge_count = 0;
		graph_traits<graph>::edge_iterator ei, ei_end;
		for (boost::tie(ei, ei_end) = edges(g); ei != ei_end; ++ei)
			put(e_index, *ei, edge_count++);


		// Test for planarity - if the graph is planar the first if code is run, if not then the else code
		// If it is non-planar faces are only found in the part of the graph that is planar
		typedef std::vector< graph_traits<graph>::edge_descriptor > vec_t;
		std::vector<vec_t> embedding(num_vertices(g));
		if (boyer_myrvold_planarity_test(boyer_myrvold_params::graph = g,
			boyer_myrvold_params::embedding =
			&embedding[0]))
		{
			std::cout << "Input graph is planar" << std::endl;

			if (i == 0)
			{
				output_file.open("faces.txt");
			}
			else
			{
				output_file.open("faces.txt", ios::out | ios::app);
			}
			

			//std::cout << std::endl << "Vertices on the faces: " << std::endl;
			vertex_output_visitor v_vis;
			planar_face_traversal(g, &embedding[0], v_vis);

			output_file.close();

			//std::cout << std::endl << "Edges on the faces: " << std::endl;
			edge_output_visitor e_vis;
			planar_face_traversal(g, &embedding[0], e_vis);
		}

		else
		{
			std::cout << "Input graph is not planar" << std::endl;
			if (i == 0)
			{
				output_file.open("faces.txt");
			}
			else
			{
				output_file.open("faces.txt", ios::out | ios::app);
			}

			//std::cout << std::endl << "Vertices on the faces: " << std::endl;
			vertex_output_visitor v_vis;
			planar_face_traversal(g, &embedding[0], v_vis);

			output_file.close();

			//std::cout << std::endl << "Edges on the faces: " << std::endl;
			edge_output_visitor e_vis;
			planar_face_traversal(g, &embedding[0], e_vis);
		}
	}
	return 0;
}
